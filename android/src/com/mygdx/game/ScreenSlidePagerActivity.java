package com.mygdx.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

/**
 * TODO: Should serve as main entry point for our app
 * TODO: rename Activity
 * TODO: add navigation drawer and implement ViewControllerInterface
 *
 */

public class ScreenSlidePagerActivity extends FragmentActivity
        implements AndroidFragmentApplication.Callbacks, AdapterView.OnItemClickListener, ViewControllerInterface{

    private final String TAG = "ScreenSliderPagerActivity";
    private final int IMAGE_FRAGMENT = 0;
    private final int KEYBOARD_FRAGMENT = 1;
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.d("OpenCV","Unable to Load");
        }
    }
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next fragments.*/
    private ViewPager mPager;

    /**The pager adapter, which provides the pages to the view pager widget.*/
    private PagerAdapter mPagerAdapter;
    private CameraBridgeViewBase mOpenCvCameraView;
    private String navigationListElements[] = {
            "Start Spotting",
            "Select Image",
            "Convert to Text",
            "Keyboard",
            "Test"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.vpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(IMAGE_FRAGMENT);

        ListView navigation_list = (ListView) findViewById(R.id.navigation_list);
        navigation_list.setAdapter(new ArrayAdapter<String>
                (this,R.layout.navigation_list,R.id.text_element,navigationListElements));
        navigation_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0: StartSpotting();
                    break;
            case 1: StartImageBrowser();
                    break;
            case 2: ConvertToText();
                    break;
            case 3: ShowKeyboard();
                    break;
        }
    }

    @Override
    public void onBackPressed() {
    	
    	switch( mPager.getCurrentItem()) {
    		
	    	case IMAGE_FRAGMENT: super.onBackPressed();
                    break;
	    	case KEYBOARD_FRAGMENT:	mPager.setCurrentItem(0);
                    break;
    	}
    }

    @Override
    public void exit() { }

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
        mPager.setCurrentItem(KEYBOARD_FRAGMENT);
        mPager.invalidate();
        FragmentFactory.getKeyboardFragment().getView().invalidate();
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(KEYBOARD_FRAGMENT);
                mPager.invalidate();
                FragmentFactory.getKeyboardFragment().getView().invalidate();
            }
        });*/
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.getInstance(position);
        }

        @Override
        public int getCount() { return FragmentFactory.WORKER_COUNT;}
    }
}