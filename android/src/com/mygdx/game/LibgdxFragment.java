package com.mygdx.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

/**
 * Created by monty on 1/13/2016.
 */
public class LibgdxFragment extends AndroidFragmentApplication implements ViewControllerInterface
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {  return initializeForView(new MyImageViewer(this));   }

    @Override
    public void StartImageBrowser() {

    }

    @Override
    public void ConvertToText() {

    }

    @Override
    public void StartSpotting() {

    }

    @Override
    public void ShowKeyboard() {

    }
}
