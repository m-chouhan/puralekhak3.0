package com.mygdx.game;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/** Libgdx Code Entry Point
* */

public class AndroidLauncher extends AndroidApplication implements ViewControllerInterface {

	static {
		// If you use opencv 2.4,
        System.loadLibrary("opencv_java");
    }
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.libgdxview);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new MyImageViewer(this), config);
//		LinearLayout gdxlayout = (LinearLayout)findViewById(R.id.libgdxlayout);
//		gdxlayout.addView(initializeForView(new MyImageViewer(this),config));
	}

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
