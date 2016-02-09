package com.mygdx.game;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by monty on 2/9/2016.
 */
public class SpottingViewAdapter {

    private ArrayList<SpotArea> SpottingsList;
    private ArrayList<SelectionBox> BoxList;

    public void addItem() {}
    public void deleteItem(SelectionBox s) {}
    public ArrayList<SelectionBox> getItemView() { return BoxList; }
    public ArrayList<SpotArea> getSpottingsArea() { return SpottingsList;}

    public void Scaled(SelectionBox selectionBox) {

    }

    public void Moved(SelectionBox selectionBox) {

    }

    public void ItemSelected(SelectionBox selectionBox) {

    }
}
