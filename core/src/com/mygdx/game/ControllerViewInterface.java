package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Created by monty on 1/13/2016.
 * View must implement this interface to receive notification and updates from background
 */
public interface ControllerViewInterface {

    void SpottingUpdated(ArrayList<Rectangle> points, String unicode);
    void OpenImage(String imagePath);
    void UnicodeSelected(String unicode);
    void FreezTemplates();
    void UnFreezTemplates();
    void UpdateProgress(int progress);
    void ShowProgressBar();
    void DismissProgressBar();
    void WriteTemplatesToFile(String fileName);
    void Reset();
    void FreeMemory();
}
