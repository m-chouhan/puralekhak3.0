package com.mygdx.game;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.CameraBridgeViewBase;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.SurfaceView;
import android.inputmethodservice.KeyboardView;
import android.view.ViewGroup.LayoutParams;

/* CC:F7:26:3A:00:3E:36:50:B5:3A:B5:40:EE:DC:80:21:68:15:1E:94
 * API_KEY:AIzaSyDjmrjBEPvgPlXtT18BQgl9GOYR3vzf304
 * */

public class FragmentView extends Fragment {
    // Store instance variables
    private int ID;
    private final String TAG = "FRAGEMENTVIEW";

    public static FragmentView newInstance(int id) {
        FragmentView fragmentFirst = new FragmentView();
        Bundle args = new Bundle();
        args.putInt("someInt", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
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
	        case 0:
	    			view = inflater.inflate(R.layout.donor_view, container, false);
	    			break;
	        case 2:
					view = inflater.inflate(R.layout.keyboard_view, container, false);
                    /*Keyboard view */
                    LinearLayout.LayoutParams parameters =
                            new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                    KeyboardView kv = new MyKeyboard(getActivity(),R.xml.keyboard_hindi);
                    kv.setLayoutParams(parameters);
                    LinearLayout keyboardlayout = (LinearLayout) view.findViewById(R.id.keyboardLayout);
                    keyboardlayout.addView(kv);
                    break;
	        case 1:
					view = inflater.inflate(R.layout.cv_main, container, false);
                    view.setTag("1");
                    CameraBridgeViewBase mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.HelloOpenCvView);
					mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
					mOpenCvCameraView.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener2) getActivity());
					//mOpenCvCameraView.enableView();
	        		break;
        }

		return view;
    }
}