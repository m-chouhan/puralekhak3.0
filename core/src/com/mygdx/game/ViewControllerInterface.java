package com.mygdx.game;

/**
 * Created by monty on 1/13/2016.
 * Controller must implement this interface to receive frontend events
 * TODO: remove dependency on ShowKeyboard
 */
public interface ViewControllerInterface {

    void StartImageBrowser();
    void ConvertToText();
    void StartSpotting();
    void TemplateSelected(final int x,final int y,final int width,final int height);
    void ShowKeyboard();

}
