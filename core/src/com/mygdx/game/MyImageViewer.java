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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.ArrayList;

/** TODO:Take image file as in input
 *  Displays image for spotting
 *  Main GUI Renderer
 */
 public class MyImageViewer extends ApplicationAdapter implements ControllerViewInterface {

    private final String TAG = "MyImageViewer";

    /*responsible for buttons */
    private Stage frontend;
    int Width,Height;

    private String imagePath="inscription.jpg";
    /* represents image from gui's perspective*/
	Sprite myImageSprite;
    /*For zoomIn,zoomOut and moving image*/
    OrthographicCamera camera;
    /*Handles custom widgets actions like scale,create new , move etc */
    InputHandler InputProcessor;
    /* Stores all custom widgets i.e spotted characters */
    ArrayList<SelectionBox> BoxList = new ArrayList<SelectionBox>();
    /* Renders all custom widgets*/
    ShapeRenderer WidgetRenderer;
    /*Renders sprites i.e myimagesprite */
    SpriteBatch batch;

    private ViewControllerInterface viewControllerInterface;
    /*Stores the current unicode used for spotting */
    private TextButton unicodeButton;

    public void setViewControllerInterface(ViewControllerInterface callback) {
        viewControllerInterface = callback;
    }

    public MyImageViewer(ViewControllerInterface callbackInterface) {
        viewControllerInterface = callbackInterface;
    }

	@Override
	public void create () {

        Width = Gdx.graphics.getWidth(); Height = Gdx.graphics.getHeight();

        /*Initializing View elements */
        camera  = new OrthographicCamera(Width,Height);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom -= 0.5;
        camera.rotate(90);
        camera.update();
        Texture img = new Texture(imagePath);
        batch = new SpriteBatch();
        WidgetRenderer = new ShapeRenderer();
        myImageSprite = new Sprite(img);
        myImageSprite.setScale(1);

        /*Buttons Initialization */
        //loadUI();
        frontend = new Stage();
        /*Setting up Input Processing */
        InputProcessor = new InputHandler(camera,BoxList);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(frontend);
        multiplexer.addProcessor(new GestureDetector(new GestureProcessor(camera, BoxList)));
        multiplexer.addProcessor(InputProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glLineWidth(8);

        batch.setProjectionMatrix(camera.combined);
        WidgetRenderer.setProjectionMatrix(camera.combined);
		batch.begin();
		    myImageSprite.draw(batch);
		batch.end();
        WidgetRenderer.setColor(Color.YELLOW);
        for(SelectionBox s:BoxList) s.Draw(WidgetRenderer);
        frontend.draw();
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
//        unicodeButton.getLabel().scaleBy(10);
        unicodeButton.getLabel().setFontScale(3);
        unicodeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                viewControllerInterface.ShowKeyboard();
            }
        });
        /*Fancy stuff for a button :) *
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

    @Override
    public void TextUpdated() {

    }

    @Override
    public void SpottingUpdated() {

    }

    @Override
    public void OpenImage(String imagePath) {

    }

    @Override
    public void UnicodeSelected(String unicode) {
        unicodeButton.getLabel().setText(Character.toString((char) (905)));
    }
}
