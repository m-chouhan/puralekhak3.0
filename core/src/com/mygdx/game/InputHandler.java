package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by maximus_prime on 12/11/15.
 * Responsible for handling all raw touch inputs - drag , tap , touch etc and updating the camera
 * accordingly.
 * TODO: Integrate with GestureProcessor
 */
public class InputHandler  extends InputAdapter {

    private final String TAG = "InputHandler";
    private final MyImageViewer myImageViewer;
    private final OrthographicCamera camera;
    private final ArrayList<SelectionBox> BoxList;
    private Vector3 InitialTouchPos = new Vector3();
    private Vector3 InitialCameraPos = new Vector3();
    private SelectionBox selectedBox = null; /*only one box can be selected at a time */

    InputHandler(MyImageViewer imViewer) {
        myImageViewer = imViewer;
        camera = imViewer.camera;
        BoxList = imViewer.BoxList;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {

        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));
        Vector2 touch2D = new Vector2(touch3D.x,touch3D.y);
        Gdx.app.log(TAG,"touchDown"+touch2D);
        for(SelectionBox s:BoxList) {
            if( s.touchDown(touch2D) ) {
                selectedBox = s;
                return true;
            }
        }
        InitialTouchPos.set(touch3D);
        InitialCameraPos.set(camera.position);

        return super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {

        Gdx.app.log(TAG,"touchDragged");

        Vector3 touch3D = camera.unproject(new Vector3(x,y,0));
        Vector2 touch2D = new Vector2(touch3D.x,touch3D.y);

        if( selectedBox != null ) {
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
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(selectedBox != null) {
            switch (selectedBox.currentState)
            {
                case MOVE:  myImageViewer.SelectionBoxMoved(selectedBox);
                            break;
                case SCALE_BOTTOM:
                case SCALE_TOP:
                            myImageViewer.SelectionBoxScaled(selectedBox);
                        break;
            }

            selectedBox.touchUp();
            selectedBox = null;
        }
        return true;
    }

}
