package com.mygdx.game;

import org.opencv.android.CameraBridgeViewBase;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.SurfaceView;
import android.inputmethodservice.KeyboardView;
import android.view.ViewGroup.LayoutParams;


/**
 * Generates Fragment View for PagerActivity
 * supports only keyboard fragment at present
 * TODO: settings page fragment
 * */

public class FragmentFactory {

    public static final int WORKER_COUNT = 2;
    private final String TAG = "FragmentFactory";

    private static LibgdxFragment libgdxFragment = null;
    private static KeyboardFragment keyboardFragment = null;

    /*for passing messages to main activity */
    public interface UpdateViewCallback{
        void UnicodeSelected(String unicode);
        void ImageviewerReady(ControllerViewInterface cvInterface);
    };

    static Fragment getInstance(int position) {

        switch (position) {

            case 0:
                    if(libgdxFragment == null )
                        libgdxFragment = new LibgdxFragment();
                    return libgdxFragment;
            case 1:
                    if(keyboardFragment == null)
                        keyboardFragment = new KeyboardFragment();
                    return keyboardFragment;
        }
        return null;
    }

    public static KeyboardFragment getKeyboardFragment() {
        return keyboardFragment;
    }
    public static LibgdxFragment getLibgdxFragment() {
        return libgdxFragment;
    }
}