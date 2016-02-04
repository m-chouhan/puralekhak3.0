package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;

/** TODO:Port the custom widgets to user Actor class
 * TODO: default image not mapping properly to image display
 *  Displays image for spotting
 *  Main GUI Renderer
 */
 public class MyImageViewer extends ApplicationAdapter implements ControllerViewInterface {

    private final String TAG = "MyImageViewer";

    /*responsible for buttons */
    private Stage frontend;
    int Width,Height;

    private String imagePath;
    /* represents image from gui's perspective*/
	Image myImage;
    Texture myImageTexture;
    /*For zoomIn,zoomOut and moving image*/
    OrthographicCamera camera;
    /*Handles custom widgets actions like scale,create new , move etc */
    InputHandler InputProcessor;
    /* Stores all custom widgets i.e spotted characters */
    ArrayList<SelectionBox> BoxList = new ArrayList<SelectionBox>();
    /* Renders all custom widgets*/
    ShapeRenderer WidgetRenderer;

    private ViewControllerInterface viewControllerInterface;
    /*Stores the current unicode used for spotting */
    private TextButton unicodeButton;

    public MyImageViewer(ViewControllerInterface callbackInterface,String imagePath) {
        viewControllerInterface = callbackInterface;
        this.imagePath = imagePath;
    }

	@Override
	public void create () {

        Width = Gdx.graphics.getWidth(); Height = Gdx.graphics.getHeight();

        /*Initializing View elements */

        WidgetRenderer = new ShapeRenderer();
        /*Opens internal image in assest/ folder as default */
        myImageTexture = new Texture(imagePath);

        /*Buttons Initialization */
        //loadUI(); not required since already implemented in android
        frontend = new Stage();
        /*required for correct rendering in android coordinate system */
        TextureRegion region = new TextureRegion(myImageTexture);
        //region.flip(false,true);
        myImage = new Image(region);
        frontend.addActor(myImage);
        camera = (OrthographicCamera) frontend.getCamera();
        /*To match the libgdx coordinate system with android coordinate system */
        camera.setToOrtho(true);

        /*Setting up Input Processing */
        InputProcessor = new InputHandler(this);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(frontend);
        multiplexer.addProcessor(new GestureDetector(new GestureProcessor(this)));
        multiplexer.addProcessor(InputProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

	@Override
	public void render () {

		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glLineWidth(8);
        frontend.draw();
        WidgetRenderer.setProjectionMatrix(camera.combined);
        WidgetRenderer.setColor(Color.YELLOW);
        for(SelectionBox s:BoxList) s.Draw(WidgetRenderer);
	}

    /*Loads frontend UI elements */
    public void loadUI() {

        frontend = new Stage();
        TextButton.TextButtonStyle textBStyle = new TextButton.TextButtonStyle();
        textBStyle.font = new BitmapFont();
        textBStyle.up = textBStyle.down = textBStyle.checked =
                new TextureRegionDrawable(new TextureRegion(new Texture("uni.png")));
        char uni = '\u0c90';
        unicodeButton = new TextButton(String.valueOf(uni),textBStyle);
        unicodeButton.getLabel().setFontScale(3);
        unicodeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                viewControllerInterface.ShowKeyboard();
            }
        });
        /*Fancy stuff for a button :) *
        unicodeButton.getLabel().scaleBy(10);
        textButton.setOrigin(textButton.getWidth()/2,textButton.getHeight()/2);
        textButton.addAction(Actions.rotateBy(180, 0.2f));
        textButton.getLabel().setRotation(90);
        textButton.scaleBy(40);
        textButton.setPosition(100, Height - 80);*/

        /* Reducing UI elemtents
        ImageButton.ImageButtonStyle imStyle = new ImageButton.ImageButtonStyle();
        imStyle.up = imStyle.down = imStyle.checked =
                new TextureRegionDrawable(new TextureRegion(new Texture("browser.png")));
        ImageButton imageButton = new ImageButton(imStyle);
        imageButton.setPosition(100, Height - 100);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                StartActivityCallback.StartImageBrowser();
            }
        });*/
        Table table = new Table();
        table.setFillParent(true);
        //table.setDebug(true); //shows table elements using lines
        table.left().top();
        table.add(unicodeButton).width(120).height(120);
        frontend.addActor(table);
    }

    /*
    * x,y,w,h are according to the view
    * actual pixel values for template selection will be different hence need to be calculated
    * */
    float horizontalRatio;
    float verticalRatio;
    private Rectangle TransformToPixelCoordinates(SelectionBox box) {
        Rectangle rect = new Rectangle();
        horizontalRatio = myImageTexture.getWidth()/myImage.getWidth();
        verticalRatio = myImageTexture.getHeight()/myImage.getHeight();
        /*calculating actual pixel coordinates */
        rect.x = box.getX()*horizontalRatio;
        rect.y = box.getY()*verticalRatio;
        rect.width = box.getWidth()*horizontalRatio;
        rect.height = box.getHeight()*verticalRatio;
        return rect;
    }
    private Vector2 TransformToCameraCoordinates(Vector2 point) {

//        float horizontalRatio = myImageTexture.getWidth()/myImage.getWidth();
//        float verticalRatio = myImageTexture.getHeight()/myImage.getHeight();
        /*calculating actual pixel coordinates */
        point.x = point.x/horizontalRatio;
        point.y = point.y/verticalRatio;
        return point;
    }
    /*Sends message to the controller to update template and adds a new selection box*/
    void CreateSelectionBoxAt( float x,float y,float width ,float height ) {
        SelectionBox box = new SelectionBox(x, y, width, height);
        BoxList.add(box);
        Rectangle rect = TransformToPixelCoordinates(box);
        viewControllerInterface.TemplateSelected((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
    }
    /*for updating template */
    void SelectionBoxMoved( SelectionBox box) {
        Rectangle rect = TransformToPixelCoordinates(box);
        viewControllerInterface.TemplateMoved((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
    }

    void SelectionBoxScaled(SelectionBox box) {
        Rectangle rect = TransformToPixelCoordinates(box);
        viewControllerInterface.TemplateResized((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
    }

    @Override
    public void TextUpdated() {

    }

    @Override
    public void SpottingUpdated(ArrayList<Vector2> points) {

        for( Vector2 point:points) {
            Gdx.app.log(TAG,"before:"+point);
            TransformToCameraCoordinates(point);
            Gdx.app.log(TAG,"after:"+point);
            CreateSelectionBoxAt(point.x,point.y,50,50);
        }
    }

    /*loads the texture of the image to open and displays it in image widget*/
    @Override
    public void OpenImage(final String imagePath) {
        /*Required since Any graphics operations directly
        involving OpenGL need to be executed on the rendering thread. */

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                myImageTexture = new Texture(Gdx.files.absolute(imagePath));
                TextureRegion region = new TextureRegion(myImageTexture);
                region.flip(false,true);
                myImage.setDrawable(new SpriteDrawable(new Sprite(region)));
                Gdx.app.log(TAG, "OpenImage:" + myImageTexture.getWidth() + myImageTexture.getHeight() +","+ myImageTexture.getDepth());
                Gdx.app.log(TAG, "OpenImage (widget):"+myImage.getWidth()+","+myImage.getHeight() );
            }
        });
    }

    @Override
    public void UnicodeSelected(String unicode) {
        unicodeButton.getLabel().setText(Character.toString((char) (905)));
    }
}
