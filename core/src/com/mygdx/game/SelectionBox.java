package com.mygdx.game;


import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by maximus_prime on 12/11/15.
 */

public class SelectionBox extends InputAdapter {


    enum States{MOVE,STATIC,SCALE_TOP,SCALE_BOTTOM};
    States currentState = States.STATIC;

    Rectangle Rect,Top_Right,Bottom_Left;
    Sprite boxLeft,boxRight;
    private boolean selected = false;
    private Vector2 InitialPos = new Vector2();
    private Vector2 InitialCenter = new Vector2();

    //Todo:initilize sprites
    SelectionBox(float x,float y,float width,float height) {

        Rect = new Rectangle(x,y,width,height);
        Top_Right = new Rectangle(0,0,20,20);
        Bottom_Left = new Rectangle(0,0,20,20);
        Top_Right.setCenter(Rect.x + Rect.width , Rect.y + Rect.height);
        Bottom_Left.setCenter(Rect.x, Rect.y);
    }

    void Draw(ShapeRenderer shapeRenderer) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(Rect.x,Rect.y,Rect.width,Rect.height);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(Top_Right.x, Top_Right.y, Top_Right.width, Top_Right.height);
        shapeRenderer.rect(Bottom_Left.x,Bottom_Left.y,Bottom_Left.width,Bottom_Left.height);
        shapeRenderer.end();

    }

    void Move(Vector2 position) {
        Rect.setCenter(position);
        Top_Right.setCenter(Rect.x + Rect.width , Rect.y + Rect.height);
        Bottom_Left.setCenter(Rect.x, Rect.y);
    }


    public boolean touchDragged(Vector2 point) {

        Vector2 delta = new Vector2(point);

        delta.sub(InitialPos);

        switch (currentState) {
            case STATIC:
                    return false;
            case MOVE:
                    Move(delta.add(InitialCenter));
                    break;
            case SCALE_TOP:
                    Top_Right.setCenter(delta.add(InitialCenter));
                    Rect.set(Rect.x,Rect.y,delta.x-Rect.x,delta.y-Rect.y);
                    break;
            case SCALE_BOTTOM:
                    Bottom_Left.setCenter(delta.add(InitialCenter));
                    Rect.set(delta.x, delta.y,
                            Rect.width+Rect.x-delta.x,Rect.height+Rect.y-delta.y);
                    //Rect.setX(delta.x);Rect.setY(delta.y);
                break;
        }
        return true;
    }

    public boolean touchUp() {

        InitialPos.set(0,0);
        currentState = States.STATIC;
        return true;
    }

    public boolean touchDown(Vector2 point) {

        if(Top_Right.contains(point)) {
            currentState = States.SCALE_TOP;
            Top_Right.getCenter(InitialCenter);
        }
        else if(Bottom_Left.contains(point)) {
            currentState = States.SCALE_BOTTOM;
            Bottom_Left.getCenter(InitialCenter);
        }
        else if( Rect.contains(point)) {
            currentState = States.MOVE;
            Rect.getCenter(InitialCenter);
        }
        else {
            currentState = States.STATIC;
            return false;
        }

        InitialPos.set(point);
        return true;
    }


    States contains(Vector2 point) {
        if( !Rect.contains(point)) return States.STATIC;

        if(Top_Right.contains(point)) return States.SCALE_TOP;
        if(Bottom_Left.contains(point)) return States.SCALE_BOTTOM;

        return States.MOVE;
    }
    public Rectangle getRect() {
        return Rect;
    }

}
