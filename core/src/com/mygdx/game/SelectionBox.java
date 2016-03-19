package com.mygdx.game;


import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
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

    /*GUI/Widget code follows -->*/
    private Color default_col = Color.RED,selection_col = Color.WHITE;


    /*All Possible states for a selection box */
    enum States{MOVE,STATIC,SCALE_TOP,SCALE_BOTTOM};
    States currentState = States.STATIC;
    Color mColor = default_col;
    Rectangle Top_Right,Bottom_Left;
    /*true if this instance is handling the input events*/
    private boolean disabled = false;
    private Vector2 InitialPos = new Vector2();
    private Vector2 InitialCenter = new Vector2();

    SelectionBox(float x,float y,float width,float height,String sym) {

        setSymbol(sym);
        Rect = new Rectangle(x,y,width,height);
        Top_Right = new Rectangle(0,0,width/3,height/3);
        Bottom_Left = new Rectangle(0,0,width/3,height/3);
        Top_Right.setCenter(Rect.x + Rect.width , Rect.y + Rect.height);
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
                    if(new_width < 50 || new_height < 50 ) return true;
                    Rect.set(Rect.x, Rect.y,new_width,new_height);
                break;
            case SCALE_BOTTOM:
                    delta.add(InitialCenter);
                    new_width = Rect.width + Rect.x - delta.x;
                    new_height = Rect.height + Rect.y - delta.y;
                    if(new_width < 50 || new_height < 50 ) return true;
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
        InitialPos.set(0,0);
        currentState = States.STATIC;
        mColor = default_col;
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
            mColor = default_col;
            return false;
        }
        mColor = selection_col;
        InitialPos.set(point);
        return true;
    }

    boolean contains(Vector2 point) {

        if( disabled ) return false;

        if(Top_Right.contains(point)) return true;
        else if(Bottom_Left.contains(point)) return true;
        else if(Rect.contains(point)) return true;

        return false;
    }

    public Rectangle getRect() { return Rect; }
    public float getX() { return Rect.getX(); }
    public float getY() { return Rect.getY(); }
    public float getWidth() { return Rect.getWidth(); }
    public float getHeight() { return Rect.getHeight(); }
    public void enable() {
        disabled = false;
        mColor = default_col;
    }
    public void disable(){
        disabled = true;

        mColor.set((float)(default_col.r*0.8),(float)(default_col.g*0.8),(float)(default_col.b*0.8),
                default_col.a);
    }
    public void SwitchState() {

        disabled = !disabled;

        if(disabled)
            mColor.set((float)(default_col.r*0.8),(float)(default_col.g*0.8),(float)(default_col.b*0.8),
                    default_col.a);
        else
            mColor = default_col;
    }

    public void setSymbol(String sym) {
        symbol = sym;
        //default_col = Util.Rainbow(Util.UnicodetoInteger(symbol));
        default_col = Util.ColorFromList(Util.UnicodetoInteger(symbol));
    }
    public String getSymbol() {return symbol; }
}
