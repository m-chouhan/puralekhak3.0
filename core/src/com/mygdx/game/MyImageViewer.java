package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/** TODO:Port the custom widgets to user Actor class
 * TODO: default image not mapping properly to image display
 *  Displays image for spotting
 *  Main GUI Renderer
 */
 public class MyImageViewer extends ApplicationAdapter implements ControllerViewInterface {

    private final String TAG = "MyImageViewer";

    /*responsible for buttons */
    private Stage mButtonStage;
    private Stage mCustomWidgetStage;
    int Width,Height;

    private String imagePath;
    /** represents image from gui's perspective*/
	Image myImage;
    Image myTemplatePreview;
    Texture myImageTexture;
    /**For zoomIn,zoomOut and moving image*/
    OrthographicCamera camera;
    /**Handles custom widgets actions like scale,create new , move etc */
    GestureProcessor InputProcessor;
    /** Stores all custom widgets i.e spotted characters */
    ArrayList<SelectionBox> BoxList = new ArrayList<SelectionBox>();
    /** Renders all custom widgets*/
    ShapeRenderer WidgetRenderer;

    private ViewControllerInterface viewControllerInterface;

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
        loadUI();
        mCustomWidgetStage = new Stage();
        /*required for correct rendering in android coordinate system */
        TextureRegion region = new TextureRegion(myImageTexture);
        region.flip(false,true);
        myImage = new Image(region);

        mCustomWidgetStage.addActor(myImage);
        myImage.setDrawable(new SpriteDrawable(new Sprite(region)));
        camera = (OrthographicCamera) mCustomWidgetStage.getCamera();
        /*To match the libgdx coordinate system with android coordinate system */
        camera.setToOrtho(true);

        /*Setting up Input Processing */
        InputProcessor = new GestureProcessor(this);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(mButtonStage);
        multiplexer.addProcessor(InputProcessor.getGestureDetector());
        Gdx.input.setInputProcessor(multiplexer);
    }

	@Override
	public void render () {

		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glLineWidth(8);
        mCustomWidgetStage.draw();
        WidgetRenderer.setProjectionMatrix(camera.combined);
        try {
            for (SelectionBox s : BoxList) s.Draw(WidgetRenderer);
        } catch (ConcurrentModificationException e) {}
        mButtonStage.draw();
	}

    /*Loads UI elements */
    public void loadUI() {

        mButtonStage = new Stage();
        ImageButton.ImageButtonStyle imStyle = new ImageButton.ImageButtonStyle();
        imStyle.up = imStyle.down = imStyle.checked =
                new TextureRegionDrawable(new TextureRegion(new Texture("plus.png")));
        ImageButton plusButton = new ImageButton(imStyle);
//        plusButton.setPosition(100, Height - 100);
        plusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CreateSelectionBoxAt(camera.position.x - 50, camera.position.y - 50, 100, 100, "");
            }
        });

        ImageButton.ImageButtonStyle minusStyle = new ImageButton.ImageButtonStyle();
        minusStyle.up = minusStyle.down = minusStyle.checked =
                new TextureRegionDrawable(new TextureRegion(new Texture("minus.png")));
        ImageButton minusButton = new ImageButton(minusStyle);
        minusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RemoveCurrentSelection();
            }
        });
        myTemplatePreview = new Image(new TextureRegion(myImageTexture,0,0,100,100));

        Table table = new Table();
        table.setFillParent(true);
        //table.setDebug(true); //shows table elements using lines
        table.left().top().padLeft(50).padTop(150);
        table.add(plusButton).width(80).height(80);
        table.row();
        table.add(minusButton).width(80).height(80);

        Table templatepreviewTable = new Table();
        templatepreviewTable.setFillParent(true);
        templatepreviewTable.setDebug(true);
        templatepreviewTable.right().padRight(35).top().padTop(35).add(myTemplatePreview);

        mButtonStage.addActor(table);
        mButtonStage.addActor(templatepreviewTable);
        /*
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
        });*/
        /*Fancy stuff for a button :) *
        unicodeButton.getLabel().scaleBy(10);
        textButton.setOrigin(textButton.getWidth()/2,textButton.getHeight()/2);
        textButton.addAction(Actions.rotateBy(180, 0.2f));
        textButton.getLabel().setRotation(90);
        textButton.scaleBy(40);
        textButton.setPosition(100, Height - 80);*/
    }

    /*
    * x,y,w,h are according to the view
    * actual pixel values for template selection will be different hence need to be calculated
    * */
//    float horizontalRatio;
//    float verticalRatio;
    private Rectangle TransformToPixelCoordinates(SelectionBox box) {
        Rectangle rect = new Rectangle();
        float horizontalRatio = myImageTexture.getWidth()/myImage.getWidth();
        float verticalRatio = myImageTexture.getHeight()/myImage.getHeight();
        /*calculating actual pixel coordinates */
        rect.x = box.getX()*horizontalRatio;
        rect.y = box.getY()*verticalRatio;
        rect.width = box.getWidth()*horizontalRatio;
        rect.height = box.getHeight()*verticalRatio;
        return rect;
    }

//    private Vector2 TransformToCameraCoordinates(Vector2 point) {
//
//        float horizontalRatio = myImageTexture.getWidth()/myImage.getWidth();
//        float verticalRatio = myImageTexture.getHeight()/myImage.getHeight();
//        /*calculating actual pixel coordinates */
//        point.x = point.x/horizontalRatio;
//        point.y = point.y/verticalRatio;
//        return point;
//    }
    /*Sends message to the controller to update template and adds a new selection box*/
    void CreateSelectionBoxAt( float x,float y,float width ,float height,String unicode ) {

        SelectionBox box = new SelectionBox(x, y, width, height,unicode);
        Rectangle rect = TransformToPixelCoordinates(box);
        if( UpdateTemplatePreview(rect)) {
            BoxList.add(box);
            viewControllerInterface.TemplateSelected((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height, unicode);
        }
    }

    void SelectBoxAt(SelectionBox box) {
        Rectangle rect = TransformToPixelCoordinates(box);
        if (UpdateTemplatePreview(rect)) {
            viewControllerInterface.TemplateSelected((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height, box.getSymbol());
        }
    }

    void RemoveCurrentSelection() {
        SelectionBox s = InputProcessor.getSelectedBox();
        BoxList.remove(s);
        InputProcessor.setSelectedBox(null);
    }

    /*for updating template */
    void SelectionBoxMoved( SelectionBox box) {
        Rectangle rect = TransformToPixelCoordinates(box);
        if( UpdateTemplatePreview(rect))
            viewControllerInterface.TemplateMoved((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height, box.getSymbol());
    }

    void SelectionBoxScaled(SelectionBox box) {
        Rectangle rect = TransformToPixelCoordinates(box);
        if(UpdateTemplatePreview(rect))
            viewControllerInterface.TemplateResized((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height, box.getSymbol());
    }

    /**Quick preview of template,
     * Updates the local copy of template (since dynamically creating bitmap in android might be heavy)
     * @param rect : new coordinates of template
     * @return : true if update was successful
    * */
    private boolean UpdateTemplatePreview(final Rectangle rect) {
        Rectangle imageRect = new Rectangle(0,0,myImageTexture.getWidth(),myImageTexture.getHeight());
        if( !imageRect.contains(rect) ) return false;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                TextureRegion region =
                        new TextureRegion(myImageTexture, (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                myTemplatePreview.setDrawable(new SpriteDrawable(new Sprite(region)));
            }
        });
        return true;
    }

    @Override
    public void SpottingUpdated(ArrayList<Rectangle> spots_list, String unicode) {

//        Gdx.app.log(TAG,"myImage:"+myImage.getImageWidth()+","+myImage.getImageHeight()
//                +",\t"+myImage.getWidth()+","+myImage.getHeight());
//        Gdx.app.log(TAG,"texture:"+myImageTexture.getWidth()+","+myImageTexture.getHeight());
        for( Rectangle rect:spots_list) {
            CreateSelectionBoxAt(rect.x,rect.y,rect.getWidth(),rect.getHeight(),unicode);
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

    /* update current selected template's unicode
    * */
    @Override
    public void UnicodeSelected(String unicode) {
        SelectionBox box = InputProcessor.getSelectedBox();
        if(box != null) box.setSymbol(unicode);
    }

    @Override
    public void FreezTemplates() {
        for(SelectionBox box:BoxList ) box.disable();
    }

    @Override
    public void UnFreezTemplates() {
        for(SelectionBox box:BoxList) box.enable();
    }

    @Override
    public void UpdateProgress(int progress) {

    }

    @Override
    public void ShowProgressBar() {

    }

    @Override
    public void DismissProgressBar() {

    }
}
