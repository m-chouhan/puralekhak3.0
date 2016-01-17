package com.mygdx.game;

/**
 * Created by maximus_prime on 12/11/15.
 * Handles input for backend UI
 * TODO: Imporve Zoom
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

class GestureProcessor implements GestureListener {

    private SpriteBatch batch;
    private BitmapFont font;
    private String message = "No gesture performed yet";
    private int w,h;
    private String TAG = "GestureProcessor";
    private OrthographicCamera camera;
    private ArrayList<SelectionBox> BoxList;

    GestureProcessor(OrthographicCamera camera,ArrayList<SelectionBox> list) {
        this.camera = camera;
        BoxList = list;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        message = "Tap performed, finger" + Integer.toString(button);
        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));

        if(count == 1)
            Gdx.app.log(TAG, "tap at " + x + ", " + y + ", count: " + count);
        else
        {
            BoxList.add(new SelectionBox(touch3D.x-25,touch3D.y-25,50,50));
            Gdx.app.log(TAG, "double tap at " + x + ", " + y + ", count: " + count);
            return true;
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        message = "Long press performed";
        Gdx.app.log(TAG,message);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        message = "Fling performed, velocity:" + Float.toString(velocityX) +
                "," + Float.toString(velocityY);
        Gdx.app.log(TAG,message);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        message = "Pan performed, delta:" + Float.toString(deltaX) +
                "," + Float.toString(deltaY);
        Gdx.app.log(TAG,message);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
         return false;
     }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        message = "Zoom performed, initial Distance:" + Float.toString(initialDistance) +
                " Distance: " + Float.toString(distance);
        Gdx.app.log(TAG,message);
        if(initialDistance > (distance+20) ) camera.zoom += 0.01;
        else if (initialDistance < (distance-20) ) camera.zoom -=0.01;
        camera.update();
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {
        message = "Pinch performed";
        Gdx.app.log(TAG,message);
        return false;
    }

}