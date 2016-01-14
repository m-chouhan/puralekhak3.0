package com.mygdx.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

/**
 * Created by monty on 1/13/2016.
 * Acts as a Delegator between Android and Libgdx since it acts as an entry point
 * for libgdx.
 * Required because ImageProcessing module has entry points through both of them
 * TODO: which needs be removed ??
 */

public class LibgdxFragment extends AndroidFragmentApplication {

    ControllerViewInterface cvDelegator;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewControllerInterface vcDelegator = (ViewControllerInterface) getActivity();
        MyImageViewer imageViewer = new MyImageViewer(vcDelegator);
        cvDelegator = imageViewer;
        return initializeForView(imageViewer);
    }

    public ControllerViewInterface getCvDelegator() { return cvDelegator; }
}
