package com.mygdx.game;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badlogic.gdx.math.Rectangle;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;

/**
 * TODO: implement ViewControllerInterface
 * TODO: remove absolute path dependency
 */

public class MainActivity extends FragmentActivity
        implements AndroidFragmentApplication.Callbacks, AdapterView.OnItemClickListener,
        ViewControllerInterface , FragmentFactory.UpdateViewCallback,DrawerLayout.DrawerListener{

    private static final int IMAGE_BROWSER = 2;
    private final String TAG = "MainActivity";
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
    /*for sending messages to libgdx module */
    private ControllerViewInterface mCvInterface;
    /**The pager adapter, which provides the pages to the view pager widget.*/
    private PagerAdapter mPagerAdapter;
    /*Preview of the template */
    private ImageView mTempatePreview;
    /*Stores Coordinates of current template to be used for creating a new bitmap
    (its a heavy operation so do it only when its necessary)*/
    private SelectionBox mCurrentTemplate;
    /* Actual template to be updated only before viewing or processing*/
    private Bitmap mCurrentBitmapTemplate;
    /*Actual inscription image as a bitmap for processing */
    private Bitmap mCurrentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puralekhak_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.vpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(IMAGE_FRAGMENT);

        DrawerLayout navigation_drawer = (DrawerLayout)findViewById(R.id.navigation_drawer);
        navigation_drawer.setDrawerListener(this);
        ListView navigation_list = (ListView) findViewById(R.id.navigation_list);
        navigation_list.setAdapter(new NavigationListAdapter(this));
        navigation_list.setOnItemClickListener(this);

        mCurrentBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.inscription);
        mCurrentTemplate = new SelectionBox(0,0,mCurrentBitmap.getWidth(),mCurrentBitmap.getHeight());
        mTempatePreview = (ImageView) findViewById(R.id.template_preview);
        mTempatePreview.setImageBitmap(mCurrentBitmap);
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
    public void StartImageBrowser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Inscription"), IMAGE_BROWSER);
    }

    /*Sends path of the image to be opened to myImageViewr */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String Image_path = RealPathUtil.getRealPathFromURI(this, data.getData());
        Log.d(TAG, Image_path);

        mCurrentBitmap = BitmapFactory.decodeFile(Image_path);
        Bitmap template = Bitmap.createBitmap(mCurrentBitmap,0,0,100,100);
        mTempatePreview.setImageBitmap(template);
        mCvInterface.OpenImage(Image_path);
        Log.d(TAG, "BitmapSize:" + mCurrentBitmap.getWidth() + "," + mCurrentBitmap.getHeight());
    }

    @Override
    public void ConvertToText() {

    }

    @Override
    public void StartSpotting() {
        final Mat original = new Mat(),template = new Mat();
        Utils.bitmapToMat(mCurrentBitmap,original);
        Utils.bitmapToMat(mCurrentBitmapTemplate, template);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                BackgroundProcess.helloworld(original,template);
            }
        });
        t.start();
    }

    @Override
    public void TemplateSelected(final int x,final int y,final int width,final int height) {

        Log.d(TAG,"Template Selected "+x+","+y+","+width+","+height);
        mCurrentTemplate = new SelectionBox(x,y,width,height);
    }

    @Override
    public void TemplateMoved(int x, int y, int width, int height) {
        TemplateSelected(x,y,width,height);
    }

    @Override
    public void TemplateResized(int x, int y, int width, int height) {
        TemplateSelected(x,y,width,height);
    }

    @Override
    public void ShowKeyboard() {
        mPager.setCurrentItem(KEYBOARD_FRAGMENT);
        mPager.invalidate();
    }

    @Override
    public void UnicodeSelected(String unicode) {
        Button button =
                (Button) FragmentFactory.getLibgdxFragment().getView().findViewById(R.id.unicodeButton);
        button.setText(unicode);
        mPager.setCurrentItem(IMAGE_FRAGMENT);
    }

    @Override
    public void ImageviewerReady(ControllerViewInterface cvInterface) {
        mCvInterface = cvInterface;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        mCurrentBitmapTemplate = Bitmap.createBitmap(mCurrentBitmap,
                (int)mCurrentTemplate.getX(),(int)mCurrentTemplate.getY(),(int)mCurrentTemplate.getWidth(),(int)mCurrentTemplate.getHeight());
        mTempatePreview.setImageBitmap(mCurrentBitmapTemplate);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) { }

    @Override
    public void onDrawerClosed(View drawerView) { }

    @Override
    public void onDrawerStateChanged(int newState) { }

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

    /* Libgdx callback since this activity passes control to libgdx */
    @Override
    public void exit() { }

}