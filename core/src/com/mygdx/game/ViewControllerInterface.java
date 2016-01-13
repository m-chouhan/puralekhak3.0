package com.mygdx.game;

/**
 * Created by monty on 1/13/2016.
 * Controller must implement this interface to receive frontend events
 */
public interface ViewControllerInterface {

    public void StartImageBrowser();
    public void ConvertToText();
    public void StartSpotting();

    /*TODO: remove this dependency */
    public void ShowKeyboard();

}
