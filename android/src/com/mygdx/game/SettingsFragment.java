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
import android.widget.TextView;

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
    private UpdateViewCallback mUVCallback;

    private boolean inhibit_spinner = true;//to stop first automatic call to spinner on initialization
    /** no of fragment rows */
    private int mPatchRows;
    /** no of fragment columns */
    private int mPatchColumns;

    private TextView mRowText,mColumnText,mFragText,mMatchingText;
    /** threshold for accumulating correlation results (tc)*/
    private float mFragment_threshold = 0.3f;
    /**HOG matching threshold*/
    private float mMatching_threshold = 0.8f;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mUVCallback = (UpdateViewCallback) activity;
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
        SeekBar fragSeek = (SeekBar)view.findViewById(R.id.frag_thresh);
        SeekBar matchingSeek = (SeekBar)view.findViewById(R.id.matching_thresh);

        rowSeek.setOnSeekBarChangeListener(this);
        colSeek.setOnSeekBarChangeListener(this);
        fragSeek.setOnSeekBarChangeListener(this);
        matchingSeek.setOnSeekBarChangeListener(this);

        mPatchRows = rowSeek.getProgress();
        mPatchColumns = colSeek.getProgress();
        mUVCallback.PatchSizeChanged(mPatchRows, mPatchColumns);

        mFragment_threshold = fragSeek.getProgress()/20f;
        mMatching_threshold = matchingSeek.getProgress()/10f;
        mUVCallback.ThresholdChanged(mFragment_threshold,mMatching_threshold);

        mRowText = (TextView)view.findViewById(R.id.rowText);
        mColumnText = (TextView)view.findViewById(R.id.colText);
        mFragText = (TextView) view.findViewById(R.id.fragment_threshold_text);
        mMatchingText = (TextView) view.findViewById(R.id.matching_threshold_text);
        return view;
    }

    /*might be required due to illegal firing of itemselected event
    public void enable() { inhibit_spinner = false; }
    public void disable() { inhibit_spinner = true; }
    */
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
                mPatchRows = progress;
                mUVCallback.PatchSizeChanged(mPatchRows, mPatchColumns);
                break;
            case R.id.seekBarCol:
                mPatchColumns = progress;
                mUVCallback.PatchSizeChanged(mPatchRows, mPatchColumns);
                break;
            case R.id.frag_thresh:
                mFragment_threshold = progress/20f;
                mUVCallback.ThresholdChanged(mFragment_threshold,mMatching_threshold);
                break;
            case R.id.matching_thresh:
                mMatching_threshold = progress/10f;
                mUVCallback.ThresholdChanged(mFragment_threshold,mMatching_threshold);
        }
        mRowText.setText("Patch Rows ["+mPatchRows+"]" );
        mColumnText.setText("Patch Column ["+mPatchColumns+"]" );
        mFragText.setText("Fragment threshold ["+mFragment_threshold+"]");
        mMatchingText.setText("Matching threshold ["+mMatching_threshold+"]");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public int getmPatchRows() { return mPatchRows; }
    public int getmPatchColumns() { return mPatchColumns; }
}
