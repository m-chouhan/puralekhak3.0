package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by monty on 1/13/2016.
 * Responsible for generating all the buttons in the GUI
 * TODO: Port whole GUI here
 * uses Native android API for now for gui
 */
public class FrontendUI extends Stage{

    /*Loads frontend UI elements */
    public void loadUI() {

        TextButton.TextButtonStyle textBStyle = new TextButton.TextButtonStyle();
        textBStyle.font = new BitmapFont();;
        textBStyle.up = textBStyle.down = textBStyle.checked =
                new TextureRegionDrawable(new TextureRegion(new Texture("uni.png")));
        TextButton textButton = new TextButton("",textBStyle);

        /*Fancy stuff for a button :) *
        textButton.setOrigin(textButton.getWidth()/2,textButton.getHeight()/2);
        textButton.addAction(Actions.rotateBy(180, 0.2f));
        textButton.getLabel().setRotation(90);
        textButton.scaleBy(40);
        textButton.setPosition(100, Height - 80);*/

        ImageButton.ImageButtonStyle imStyle = new ImageButton.ImageButtonStyle();
        imStyle.up = imStyle.down = imStyle.checked =
                new TextureRegionDrawable(new TextureRegion(new Texture("browser.png")));
        ImageButton imageButton = new ImageButton(imStyle);
        /*imageButton.setPosition(100, Height - 100);
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
        table.add(imageButton).width(100);
        table.row();
        table.add(textButton).width(100).height(100);
        addActor(table);
    }
}
