package com.mygdx.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
        implements CameraBridgeViewBase.CvCameraViewListener2,AndroidFragmentApplication.Callbacks {

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
            "This",
            "Is",
            "A",
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
        mPager.setCurrentItem(0);

        ListView navigation_list = (ListView) findViewById(R.id.navigation_list);
        navigation_list.setAdapter(new ArrayAdapter<String>
                (this,R.layout.navigation_list,R.id.text_element,navigationListElements));
    }

    @Override
    public void onBackPressed() {
    	
    	switch( mPager.getCurrentItem()) {
    		
	    	case 0: super.onBackPressed();
                    break;
	    	case 1:	mPager.setCurrentItem(0);
	    			break;
    	}
    }

    @Override
    public void onCameraViewStarted(int width, int height) { }

    @Override
    public void onCameraViewStopped() { }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.gray();
    }

    @Override
    public void exit() { }

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