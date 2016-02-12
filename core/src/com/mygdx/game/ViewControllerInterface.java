package com.mygdx.game;

/**
 * Created by monty on 1/13/2016.
 * Controller must implement this interface to receive frontend events
 */
public interface ViewControllerInterface {

    void StartImageBrowser();
    void ConvertToText();
    void StartSpotting();
    void TemplateSelected(final int x, final int y, final int width, final int height, String unicode);
    void TemplateMoved(final int x, final int y, final int width, final int height, String unicode);
    void TemplateResized(final int x, final int y, final int width, final int height, String unicode);
    void ShowKeyboard(CharSequence previewText);
}
