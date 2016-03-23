package com.mygdx.game;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by monty on 2/14/2016.
 * Changes default configuration values used in the application
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener ,SeekBar.OnSeekBarChangeListener{

    private final String TAG = "Settings Fragment";
    private ArrayList<String> mSupportedLanguages;
    private ArrayList<Integer> ResourceIds;
    private FragmentFactory.UpdateViewCallback mUVCallback;

    private boolean inhibit_spinner = true;//to stop first automatic call to spinner on initialization
    /** no of fragment rows */
    private int mFragmentRows;
    /** no of fragment columns */
    private int mFragmentCols;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mUVCallback = (FragmentFactory.UpdateViewCallback) activity;
        mSupportedLanguages = new ArrayList<String>();
        ResourceIds = new ArrayList<Integer>();
        for( Field f: R.xml.class.getDeclaredFields() ) {
            f.setAccessible(true);
            try {
                String str = f.getName();
                if(str.contains("keyboard")) {
                    mSupportedLanguages.add(f.getName());
                    ResourceIds.add((Integer) f.get(null));
                }
                Log.d(TAG, f.getName() + ":" + f.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_page,container,false);
        Spinner spinner = (Spinner) view.findViewById(R.id.language_selector);
        ArrayAdapter<String> spinnerArrayAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mSupportedLanguages); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);

        SeekBar rowSeek = (SeekBar)view.findViewById(R.id.seekBarRow);
        SeekBar colSeek = (SeekBar)view.findViewById(R.id.seekBarCol);
        rowSeek.setOnSeekBarChangeListener(this);
        colSeek.setOnSeekBarChangeListener(this);
        mFragmentRows = rowSeek.getProgress();
        mFragmentCols = colSeek.getProgress();

        return view;
    }

    public void enable() { inhibit_spinner = false; }
    public void disable() { inhibit_spinner = true; }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(inhibit_spinner) {
            inhibit_spinner = false;
            return;
        }
        final String item = (String) parent.getItemAtPosition(position);
        Log.d(TAG,item+" selected");
        mUVCallback.KeyboardSelected(ResourceIds.get(position));
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Log.d(TAG,"Nothing selected");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()) {
            case R.id.seekBarRow:
                mFragmentRows = progress;
                break;
            case R.id.seekBarCol:
                mFragmentCols = progress;
                break;
        }
        mUVCallback.FragmentSizeChanged(mFragmentRows,mFragmentCols);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public int getmFragmentRows() { return mFragmentRows; }
    public int getmFragmentCols() { return mFragmentCols; }
}
