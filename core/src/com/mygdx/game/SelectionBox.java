package com.mygdx.game;


import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by maximus_prime on 12/11/15.
 * Represents a spotted character as a widget
 * TODO:Separate model data from view by using spotArea class
 * (that's why i embedded a controller inside the model to change its behaviour on selection )
 */

public class SelectionBox extends InputAdapter {

    /*actual spotting area */
    private Rectangle Rect;
    private String symbol;

    private final SpottingViewAdapter Adapter;
    /*GUI/Widget code follows -->*/
    private final Color default_col = Color.RED,selection_col = Color.GOLD;
    /*All Possible states for a selection box */
    enum States{MOVE,STATIC,SCALE_TOP,SCALE_BOTTOM};
    private States currentState = States.STATIC;

    Color mColor = default_col;
    Rectangle Top_Right,Bottom_Left;
    /*true if this instance is handling the input events*/
    private boolean selected = false;
    private Vector2 InitialPos = new Vector2();
    private Vector2 InitialCenter = new Vector2();

    SelectionBox(SpottingViewAdapter adapter,SpotArea area) {

        Adapter = adapter;
        Rect = new Rectangle(area.x,area.y,area.width,area.height);
        Top_Right = new Rectangle(0,0,40,40);
        Bottom_Left = new Rectangle(0,0,40,40);
        Top_Right.setCenter(Rect.x + Rect.width, Rect.y + Rect.height);
        Bottom_Left.setCenter(Rect.x, Rect.y);
    }


    void Draw(ShapeRenderer shapeRenderer) {

        Color prevColor = shapeRenderer.getColor();
        shapeRenderer.setColor(mColor);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(Rect.x, Rect.y, Rect.width, Rect.height);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(Top_Right.x, Top_Right.y, Top_Right.width, Top_Right.height);
        shapeRenderer.rect(Bottom_Left.x, Bottom_Left.y, Bottom_Left.width, Bottom_Left.height);
        shapeRenderer.end();

        shapeRenderer.setColor(prevColor);
    }

    void Move(Vector2 position) {
        Rect.setCenter(position);
        Top_Right.setCenter(Rect.x + Rect.width, Rect.y + Rect.height);
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
        //reset to initial condition
        InitialPos.set(0, 0);
        switchState(States.STATIC);
        mColor = default_col;
        return true;
    }

    public boolean touchDown(Vector2 point) {

        if(Top_Right.contains(point)) {
            switchState(States.SCALE_TOP);
            Top_Right.getCenter(InitialCenter);
        }
        else if(Bottom_Left.contains(point)) {
            switchState(States.SCALE_BOTTOM);
            Bottom_Left.getCenter(InitialCenter);
        }
        else if( Rect.contains(point)) {
            switchState(States.MOVE);
            Rect.getCenter(InitialCenter);
        }
        else {
            switchState(States.STATIC);
            mColor = default_col;
            return false;
        }

        Adapter.ItemSelected(this);
        mColor = selection_col;
        InitialPos.set(point);
        return true;
    }

    public float getX() { return Rect.getX(); }
    public float getY() { return Rect.getY(); }
    public float getWidth() { return Rect.getWidth(); }
    public float getHeight() { return Rect.getHeight(); }

    /** changes state of the selection box and takes action accordingly
     *  for optimization, event will be thrown when a state completes, not when it starts
     * @param state new state of the selection box
     */
    public void switchState(States state) {
        States prevstate = currentState;
        currentState = state;
        switch (prevstate) {
            case SCALE_TOP:
            case SCALE_BOTTOM:
                Adapter.Scaled(this);
                break;
            case MOVE:
                Adapter.Moved(this);
        }
    }
    public States getCurrentState() {return currentState; }
    public void setSymbol(String sym) { symbol = sym; }
    public String getSymbol() {return symbol; }
}
