package com.mygdx.game;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

 public class AndroidLauncher extends AndroidApplication implements MyImageViewer.CallbackInterface {

	static {
		// If you use opencv 2.4,
		System.loadLibrary("opencv_java");
		//System.loadLibrary("opencv_java3");
	}
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Mat test = new Mat(200, 200, CvType.CV_8UC1);
//		Imgproc.equalizeHist(test, test);
		setContentView(R.layout.libgdxview);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new MyImageViewer(this), config);
		LinearLayout gdxlayout = (LinearLayout)findViewById(R.id.libgdxlayout);
		gdxlayout.addView(initializeForView(new MyImageViewer(this),config));
	}

	 @Override
	 public String StartImageBrowser() {
		 return null;
	 }
 }
