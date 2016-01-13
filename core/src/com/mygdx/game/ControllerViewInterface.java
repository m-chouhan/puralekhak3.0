package com.mygdx.game;

/**
 * Created by monty on 1/13/2016.
 * View must implement this interface to receive notification and updates from background
 */
public interface ControllerViewInterface {

    public void TextUpdated();
    public void SpottingUpdated();
    public void OpenImage(String imagePath);
}
