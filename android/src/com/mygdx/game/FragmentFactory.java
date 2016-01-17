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

public class FragmentFactory extends Fragment implements View.OnClickListener{

    public static final int WORKER_COUNT = 2;
    private final String TAG = "FragmentFactory";

    private static LibgdxFragment libgdxFragment = null;
    private static Fragment keyboardFragment = null;
    // Store instance variables since multiple fragments are possible
    private int ID;

    /*for passing messages to main activity */
    public interface UpdateViewCallback{
        void UnicodeSelected(String unicode);
        void ImageviewerReady(ControllerViewInterface cvInterface);
    };

    private UpdateViewCallback mCallback;
    private EditText unicodeTextEditor;

    private static FragmentFactory newInstance(int id) {
        FragmentFactory fragmentFirst = new FragmentFactory();
        Bundle args = new Bundle();
        args.putInt("ID", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    static Fragment getInstance(int position) {

        switch (position) {

            case 0:
                    if(libgdxFragment == null )
                        libgdxFragment = new LibgdxFragment();
                    return libgdxFragment;
            case 1:
                    if(keyboardFragment == null)
                        keyboardFragment = newInstance(1);
                    return keyboardFragment;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getArguments().getInt("ID", 1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (UpdateViewCallback) activity;
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
                    ImageButton imageButton = (ImageButton)view.findViewById(R.id.ok_button);
                    imageButton.setOnClickListener(this);
                    unicodeTextEditor = (EditText)view.findViewById(R.id.unicodeedit);
                    Log.d(TAG,"Keyboard Inflated");
                    break;
        }
		return view;
    }

    @Override
    public void onClick(View v) {
        mCallback.UnicodeSelected(unicodeTextEditor.getText().toString());
    }

    public static Fragment getKeyboardFragment() {
        return keyboardFragment;
    }
    public static LibgdxFragment getLibgdxFragment() {
        return libgdxFragment;
    }
}