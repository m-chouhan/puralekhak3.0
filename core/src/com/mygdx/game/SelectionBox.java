package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sun.rmi.runtime.Log;

/**
 * Created by maximus_prime on 12/11/15.
 * Represents a spotted character as a widget
 * TODO:Separate model data from view by using spotArea class
 * (that's why i embedded a controller inside the model to change its behaviour on selection )
 */

public class SelectionBox extends InputAdapter {

    private final String TAG = "SelectionBox";
    /*actual spotting area */
    private Rectangle Rect;
    private String symbol;

    /*GUI/Widget code follows -->*/
    /**Color when template is selected */
    private final Color SELECTION_COL = Color.WHITE;
    private final Color mTemplateColor;;
    /**Current color for drawing*/
    private final Color mColor;
    /**All Possible states for a selection box */
    enum States{MOVE,STATIC,SCALE_TOP,SCALE_BOTTOM};
    States currentState = States.STATIC;
    Rectangle Top_Right,Bottom_Left;
    /**true if this instance is handling the input events*/
    private boolean disabled = false;
    private Vector2 InitialPos = new Vector2();
    private Vector2 InitialCenter = new Vector2();

    SelectionBox(float x,float y,float width,float height,String sym) {

        mTemplateColor = new Color(Color.RED);
        mColor = new Color(mTemplateColor);
        setSymbol(sym);
        Rect = new Rectangle(x,y,width,height);
        Top_Right = new Rectangle(0,0,width/3,height/3);
        Bottom_Left = new Rectangle(0,0,width/3,height/3);
        Top_Right.setCenter(Rect.x + Rect.width, Rect.y + Rect.height);
        Bottom_Left.setCenter(Rect.x, Rect.y);
    }


    void Draw(ShapeRenderer shapeRenderer) {

        Color prevColor = shapeRenderer.getColor();
        shapeRenderer.setColor(mColor);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(Rect.x, Rect.y, Rect.width, Rect.height);
//        shapeRenderer.end();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
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
        float new_width = 0,new_height = 0;

        switch (currentState) {
            case STATIC:
                    return false;
            case MOVE:
                    Move(delta.add(InitialCenter));
                    return true;
            case SCALE_TOP:
                    delta.add(InitialCenter);
                    new_width = delta.x - Rect.x;
                    new_height = delta.y - Rect.y;
                    if(new_width < 40 || new_height < 40 ) return true;
                    Rect.set(Rect.x, Rect.y,new_width,new_height);
                break;
            case SCALE_BOTTOM:
                    delta.add(InitialCenter);
                    new_width = Rect.width + Rect.x - delta.x;
                    new_height = Rect.height + Rect.y - delta.y;
                    if(new_width < 40 || new_height < 40 ) return true;
                    Rect.set(delta.x, delta.y,new_width,new_height);
                break;
        }

        Top_Right.setSize(Rect.width/3, Rect.height/3);
        Bottom_Left.setSize(Rect.width / 3, Rect.height / 3);
        Top_Right.setCenter(Rect.x + Rect.width, Rect.y + Rect.height);
        Bottom_Left.setCenter(Rect.x, Rect.y);
        return true;
    }

    public boolean touchUp() {
        //reset to initial condition
        InitialPos.set(0, 0);
        currentState = States.STATIC;
        if( !disabled ) mColor.set(mTemplateColor);
        return true;
    }

    public boolean touchDown(Vector2 point) {

        if( disabled ) return false;

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
            mColor.set(mTemplateColor);
            return false;
        }
        mColor.set(SELECTION_COL);
        InitialPos.set(point);
        return true;
    }

    boolean longPress(Vector2 point) {

        if( Rect.contains(point)) {
            SwitchState();
            return true;
        }
        return false;
    }
    boolean contains(Vector2 point) {

        if( disabled ) return false;
        if(Top_Right.contains(point) || Bottom_Left.contains(point) || Rect.contains(point) ) return true;
        return false;
    }

    public float getX() { return Rect.getX(); }
    public float getY() { return Rect.getY(); }
    public float getWidth() { return Rect.getWidth(); }
    public float getHeight() { return Rect.getHeight(); }

    public void enable() {
        disabled = false;
        mColor.set(mTemplateColor);
//        Gdx.app.log(TAG,"Enable"+mColor.toString());
    }

    public void disable(){
        disabled = true;
        float r = mTemplateColor.r*0.5f;
        float g = mTemplateColor.g*0.5f;
        float b = mTemplateColor.b*0.5f;
        mColor.set(r, g, b, mTemplateColor.a);
//        Gdx.app.log(TAG, "Disable"+mColor.toString());
    }

    public void SwitchState() {

        disabled = !disabled;

        if(disabled) {
            float r = mTemplateColor.r*0.5f;
            float g = mTemplateColor.g*0.5f;
            float b = mTemplateColor.b*0.5f;
            mColor.set(r,g,b,mTemplateColor.a);
        }
        else
            mColor.set(mTemplateColor);
    }

    public void setSymbol(String sym) {
        symbol = sym;
        //mTemplateColor = Util.Rainbow(Util.UnicodetoInteger(symbol));
        mTemplateColor.set(Util.ColorFromList(Util.UnicodetoInteger(symbol)));
        mColor.set(mTemplateColor);
    }
    public String getSymbol() {return symbol; }

    public boolean isSelected() {
        if( currentState == States.MOVE || currentState == States.SCALE_BOTTOM ||
                currentState == States.SCALE_TOP )
            return true;
        return false;
    }
}
