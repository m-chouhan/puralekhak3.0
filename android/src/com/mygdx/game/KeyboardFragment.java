package com.mygdx.game;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by monty on 2/12/2016.
 */
public class KeyboardFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "KeyboardFragment";

    private View mFragmentView = null;
    private KeyboardView mKeyboardView = null;
    private UpdateViewCallback mCallback;
    private EditText unicodeTextEditor;
    private Activity parentActivity;
    private int mKeyboard_id = R.xml.keyboard_bengali;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (UpdateViewCallback) activity;
        parentActivity = activity;
        Log.d(TAG,"onAttach");
    }

    /** Inflate the view for the fragment based on layout XML*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.keyboard_view, container, false);
        mKeyboardView = new MyKeyboard(getActivity(),mKeyboard_id);
        //kv.setLayoutParams(parameters);
        LinearLayout keyboardlayout = (LinearLayout) mFragmentView.findViewById(R.id.keyboardLayout);
        keyboardlayout.addView(mKeyboardView);
        ImageButton imageButton = (ImageButton) mFragmentView.findViewById(R.id.ok_button);
        imageButton.setOnClickListener(this);
        unicodeTextEditor = (EditText) mFragmentView.findViewById(R.id.unicodeedit);
        Log.d(TAG, "OnCreate View Keyboard Inflated");
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        mCallback.UnicodeEdited(unicodeTextEditor.getText().toString());
    }

    public void setText(CharSequence charSequence) {
        unicodeTextEditor.setText(charSequence);
    }

    /**required to solve rendering problem on opening new image
     * */
    public void refreshView() {
        LinearLayout keyboardlayout = (LinearLayout) mFragmentView.findViewById(R.id.keyboardLayout);
        keyboardlayout.removeView(mKeyboardView);
        mKeyboardView = new MyKeyboard(parentActivity,mKeyboard_id);
        keyboardlayout.addView(mKeyboardView);
        mFragmentView.invalidate();
    }

    public void setKeyboard(int keyboard_id) {

        if(mKeyboard_id == keyboard_id ) return;
        mKeyboard_id = keyboard_id;
        KeyboardView kv = new MyKeyboard(parentActivity,mKeyboard_id);
        LinearLayout keyboardlayout = (LinearLayout) mFragmentView.findViewById(R.id.keyboardLayout);
        keyboardlayout.removeView(mKeyboardView);
        keyboardlayout.addView(kv);
        mKeyboardView = kv;
    }
}