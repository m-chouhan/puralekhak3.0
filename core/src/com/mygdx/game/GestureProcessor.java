package com.mygdx.game;

/**
 * Created by maximus_prime on 12/11/15.
 * Handles input for backend UI
 * TODO: Improve Zoom
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

class GestureProcessor implements GestureListener {

    private final String TAG = "GestureProcessor";
    private String message = "No gesture performed yet";
    private final MyImageViewer imageViewer;
    private final OrthographicCamera camera;
    private final ArrayList<SelectionBox> BoxList;

    private Vector3 InitialTouchPos = new Vector3();
    private Vector3 InitialCameraPos = new Vector3();
    private SelectionBox selectedBox = null; /*only one box can be selected at a time */

    GestureProcessor(MyImageViewer imViewer) {
        imageViewer = imViewer;
        camera = imViewer.camera;
        BoxList = imViewer.BoxList;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {

        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));
        Vector2 touch2D = new Vector2(touch3D.x,touch3D.y);
        Gdx.app.log(TAG,"touchDown"+touch2D);

        if( selectedBox != null && selectedBox.contains(touch2D) ) {
            selectedBox.touchDown(touch2D);
            return true;
        }
        for(SelectionBox s:BoxList) {
            if( s.contains(touch2D) ) {
                s.touchDown(touch2D);
                setSelectedBox(s);
                return true;
            }
        }
//        setSelectedBox(null);
        InitialTouchPos.set(touch3D);
        InitialCameraPos.set(camera.position);

        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        message = "Tap performed, finger" + Integer.toString(button);
        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));

//        if(count == 1)
//            Gdx.app.log(TAG, "tap at " + x + ", " + y + ", count: " + count);
        if(count > 1)
        {
            imageViewer.CreateSelectionBoxAt(touch3D.x-100,touch3D.y-100,200,200,"");
            Gdx.app.log(TAG, "double tap at " + x + ", " + y + ", count: " + count);
            return true;
        }
        return false;
    }

    /* handles pinch and zoom events */
    @Override
    public boolean zoom(float initialDistance, float distance) {
        message = "Zoom performed, initial Distance:" + Float.toString(initialDistance) +
                " Distance: " + Float.toString(distance);
        //Gdx.app.log(TAG,message);
        if(initialDistance > (distance+20) ) camera.zoom += 0.01;
        else if (initialDistance < (distance-20) && camera.zoom > 0.1f ) camera.zoom -=0.01;
        camera.update();
        return true;
    }

    /*These events are not required for now */
    @Override
    public boolean longPress(float x, float y) {
        message = "Long press performed";
        //Gdx.app.log(TAG,message);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        message = "Fling performed, velocity:" + Float.toString(velocityX) +
                "," + Float.toString(velocityY);
        //Gdx.app.log(TAG,message);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        message = "Pan performed, delta:" + Float.toString(deltaX) +
                "," + Float.toString(deltaY);
        //Gdx.app.log(TAG,message);

        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));
        Vector2 touch2D = new Vector2(touch3D.x,touch3D.y);

        if( selectedBox != null && selectedBox.contains(touch2D) ) {
            selectedBox.touchDragged(touch2D);
            return true;
        }

        Vector3 direction = new Vector3(touch3D);
        direction.sub(InitialTouchPos);//direction vector
        direction.scl(-0.9f); //scale direction vector
        camera.position.set(direction.add(InitialCameraPos));
        camera.update();
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        if(selectedBox != null) {
            switch (selectedBox.currentState)
            {
                case MOVE:  imageViewer.SelectionBoxMoved(selectedBox);
                    break;
                case SCALE_BOTTOM:
                case SCALE_TOP:
                    imageViewer.SelectionBoxScaled(selectedBox);
                    break;
            }
        }
        return true;
     }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {
        message = "Pinch performed";
        //Gdx.app.log(TAG,message);
        return false;
    }

    public void setSelectedBox(SelectionBox s) {
        if(selectedBox != null ) selectedBox.touchUp();
        selectedBox = s;
        if(selectedBox != null) imageViewer.SelectBoxAt(selectedBox);
    }
    public SelectionBox getSelectedBox() { return selectedBox;}
}