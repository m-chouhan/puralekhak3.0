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

    private FragmentFactory.UpdateViewCallback mCallback;
    private EditText unicodeTextEditor;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (FragmentFactory.UpdateViewCallback) activity;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.keyboard_view, container, false);
        LinearLayout.LayoutParams parameters =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        KeyboardView kv = new MyKeyboard(getActivity(),R.xml.keyboard_hindi);
        kv.setLayoutParams(parameters);
        LinearLayout keyboardlayout = (LinearLayout) view.findViewById(R.id.keyboardLayout);
        keyboardlayout.addView(kv);
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.ok_button);
        imageButton.setOnClickListener(this);
        unicodeTextEditor = (EditText)view.findViewById(R.id.unicodeedit);
        Log.d(TAG, "Keyboard Inflated");
        return view;
    }

    @Override
    public void onClick(View v) {
        mCallback.UnicodeSelected(unicodeTextEditor.getText().toString());
    }

    public void setText(CharSequence charSequence) {
        unicodeTextEditor.setText(charSequence);
    }
}