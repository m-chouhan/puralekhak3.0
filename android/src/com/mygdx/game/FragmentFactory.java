package com.mygdx.game;

import org.opencv.android.CameraBridgeViewBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.SurfaceView;
import android.inputmethodservice.KeyboardView;
import android.view.ViewGroup.LayoutParams;

/**
 * Generates Fragment View for PagerActivity
 * TODO:Add libgdx fragment
 * */

public class FragmentFactory extends Fragment {

    public static final int WORKER_COUNT = 2;
    private final String TAG = "FragmentFactory";

    private static Fragment libgdxFragment = null;
    private static Fragment keyboardFragment = null;
    // Store instance variables
    private int ID;

    private static FragmentFactory newInstance(int id) {
        FragmentFactory fragmentFirst = new FragmentFactory();
        Bundle args = new Bundle();
        args.putInt("someInt", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static Fragment getInstance(int position) {

        switch (position) {

            case 0:
                    if(libgdxFragment == null ) libgdxFragment = new LibgdxFragment();//newInstance(0);
                    return libgdxFragment;
            case 1:
                    if(keyboardFragment == null) keyboardFragment = newInstance(1);
                    return keyboardFragment;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getArguments().getInt("someInt", 1);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
    	
        View view = null;
        switch(ID) {

	        case 1:
					view = inflater.inflate(R.layout.keyboard_view, container, false);
                    /*Keyboard view */
                    LinearLayout.LayoutParams parameters =
                            new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                    KeyboardView kv = new MyKeyboard(getActivity(),R.xml.keyboard_hindi);
                    kv.setLayoutParams(parameters);
                    LinearLayout keyboardlayout = (LinearLayout) view.findViewById(R.id.keyboardLayout);
                    keyboardlayout.addView(kv);
                    Log.d(TAG,"Keyboard Inflated");
                    break;
	        case 0:
					view = inflater.inflate(R.layout.cv_main, container, false);
                    view.setTag("1");
                    CameraBridgeViewBase mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.HelloOpenCvView);
					mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
					mOpenCvCameraView.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener2) getActivity());
					//mOpenCvCameraView.enableView();
                    Log.d(TAG,"cv_main Inflated");
                    break;
        }

		return view;
    }

}