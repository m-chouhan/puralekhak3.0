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
    private FragmentFactory.UpdateViewCallback mCallback;
    private EditText unicodeTextEditor;
    private Activity parentActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (FragmentFactory.UpdateViewCallback) activity;
        parentActivity = activity;
    }

    /** Inflate the view for the fragment based on layout XML*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.keyboard_view, container, false);
        mKeyboardView = new MyKeyboard(getActivity(),R.xml.keyboard_hindi);
        //kv.setLayoutParams(parameters);
        LinearLayout keyboardlayout = (LinearLayout) mFragmentView.findViewById(R.id.keyboardLayout);
        keyboardlayout.addView(mKeyboardView);
        ImageButton imageButton = (ImageButton) mFragmentView.findViewById(R.id.ok_button);
        imageButton.setOnClickListener(this);
        unicodeTextEditor = (EditText) mFragmentView.findViewById(R.id.unicodeedit);
        Log.d(TAG, "Keyboard Inflated");
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        mCallback.UnicodeSelected(unicodeTextEditor.getText().toString());
    }

    public void setText(CharSequence charSequence) {
        unicodeTextEditor.setText(charSequence);
    }

    public void setKeyboard(int keyboard_id) {

        KeyboardView kv = new MyKeyboard(parentActivity,keyboard_id);
        LinearLayout keyboardlayout = (LinearLayout) mFragmentView.findViewById(R.id.keyboardLayout);
        keyboardlayout.removeView(mKeyboardView);
        keyboardlayout.addView(kv);
        keyboardlayout.invalidate();
        mKeyboardView = kv;
    }
}