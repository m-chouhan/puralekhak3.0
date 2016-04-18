package com.mygdx.game;

/**
 * Created by maximus_prime on 12/11/15.
 * Handles input for backend UI
 * TODO: Improve Zoom
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import sun.rmi.runtime.Log;

class GestureProcessor implements GestureListener {

    private final String TAG = "GestureProcessor";
    private ArrayList<Integer> ActivePointerList = new ArrayList<Integer>();

    /** Class for delegating *touchUp event to gesture processor class
     * */
    private class mGestureDetector extends GestureDetector {

        private GestureProcessor mGestureProcessor;
        public mGestureDetector(GestureProcessor listener) {
            super(listener);
            mGestureProcessor = listener;
        }

        @Override
        public boolean touchUp(float x, float y, int pointer, int button) {
            mGestureProcessor.touchUp(x,y,pointer, button);
            return super.touchUp(x, y, pointer, button);
        }
    }

    public GestureDetector getGestureDetector() {
        return new mGestureDetector(this);
    }

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

        ActivePointerList.add(pointer);
        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));
        Vector2 touch2D = new Vector2(touch3D.x,touch3D.y);
        Gdx.app.log(TAG,"touchDown"+touch2D+","+pointer);

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

    private boolean touchUp(float x, float y, int pointer, int button) {

        ActivePointerList.remove(new Integer(pointer));
        Gdx.app.log(TAG, "TouchUp:" + pointer);
        if( !ActivePointerList.isEmpty() ) {
            Integer active_pointer = ActivePointerList.get(0);
            Gdx.app.log(TAG,"Still Active:"+active_pointer);
            //touchDown(Gdx.input.getX(active_pointer), Gdx.input.getY(active_pointer),active_pointer,0);
            Vector3 touch3D = camera.unproject(new Vector3(Gdx.input.getX(active_pointer), Gdx.input.getY(active_pointer), 0));
            InitialTouchPos.set(touch3D);
            InitialCameraPos.set(camera.position);
        }
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

    private float mPrevDistance;
    /** handles pinch and zoom events */
    @Override
    public boolean zoom(float initialDistance, float distance) {
        message = "Zoom performed, initial Distance:" + Float.toString(initialDistance) +
                " Distance: " + Float.toString(distance);
        //Gdx.app.log(TAG,message);
//        if(initialDistance > (distance+20) ) camera.zoom += 0.01;
//        else if (initialDistance < (distance-20) && camera.zoom > 0.1f ) camera.zoom -=0.01;
        if(mPrevDistance > distance ) camera.zoom += 0.01;
        else if( mPrevDistance < distance && camera.zoom > 0.1f ) camera.zoom -= 0.01;
        camera.update();
        mPrevDistance = distance;
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        message = "Long press performed";
        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));
        Vector2 touch2D = new Vector2(touch3D.x,touch3D.y);
        //Gdx.app.log(TAG,message);
        for(SelectionBox s:BoxList) {
            if( s.longPress(touch2D) ) {
                Gdx.input.vibrate(200);
                return true;
            }
        }
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
        direction.add(InitialCameraPos);

        if( imageViewer.getBoundingRectangle().contains(direction.x,direction.y)) {
            camera.position.set(direction);
            camera.update();
            return true;
        }
        return false;
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