package com.mygdx.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;

/**
 * TODO: remove absolute path dependency
 */

public class MainActivity extends FragmentActivity
        implements AndroidFragmentApplication.Callbacks, AdapterView.OnItemClickListener,
        ViewControllerInterface , UpdateViewCallback,DrawerLayout.DrawerListener{

    /** to identify onactivityresult */
    private static final int IMAGE_BROWSER = 2;
    private final String TAG = "MainActivity";
    /**Ids of different fragments */
    private static final int IMAGE_FRAGMENT = 1;
    private static final int KEYBOARD_FRAGMENT = 2;

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
    /**for sending messages to libgdx module */
    private ControllerViewInterface mCvInterface;
    /**The pager adapter, which provides the pages to the view pager widget.*/
    private PagerAdapter mPagerAdapter;
    /**Imageview showing Preview of the template (update when drawer is opened*/
    private ImageView mTempatePreview;
    /**Default image to be displayed when no template is present*/
    private Drawable mDefaultPreview;
    /**Stores Coordinates of current template to be used for creating a new bitmap
    (its a heavy operation so do it only when its necessary)*/
    private Rectangle mCurrentTemplateRect;
    /** Actual template to be updated only before viewing or processing*/
    private Bitmap mCurrentBitmapTemplate;
    /**Actual inscription image as a bitmap for processing */
    private Bitmap mCurrentBitmap;
    private DrawerLayout mNavigation_drawer;
    /**Dimensions of patch inside a template*/
    private int mPatchRows = 0,mPatchColumns = 0;
    /**Unicode corresponding to current template*/
    private String mUnicode = "";
    private float mFragmentThreshold;
    private float mMatchingThreshold;
    /**Path to currently open Image*/
    private String mImage_path;
    /**For preventing sleep during processing*/
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puralekhak_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.vpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(IMAGE_FRAGMENT);

        mNavigation_drawer = (DrawerLayout)findViewById(R.id.navigation_drawer);
        mNavigation_drawer.setDrawerListener(this);
        ListView navigation_list = (ListView) findViewById(R.id.navigation_list);
        navigation_list.setAdapter(new NavigationListAdapter(this));
        navigation_list.setOnItemClickListener(this);

        mCurrentBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.inscription);
        mTempatePreview = (ImageView) findViewById(R.id.template_preview);
        mDefaultPreview = getResources().getDrawable(R.drawable.titleimg);
        mTempatePreview.setImageDrawable(mDefaultPreview);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "OnTrim!!");
        if(mCurrentBitmap != null ) {
            mCurrentBitmap.recycle();
            mCurrentBitmap = null;
        }
        if(mCurrentBitmapTemplate != null) {
            mCurrentBitmapTemplate.recycle();
            mCurrentBitmapTemplate = null;
        }
        if(mWakeLock.isHeld()) mCvInterface.FreeMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume!!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause!!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"OnStop!!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy!!");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mCvInterface.FreeMemory();
        if(mCurrentBitmap != null ) {
            mCurrentBitmap.recycle();
            mCurrentBitmap = null;
        }
        if(mCurrentBitmapTemplate != null) {
            mCurrentBitmapTemplate.recycle();
            mCurrentBitmapTemplate = null;
        }
        mDefaultPreview = null;
        Log.d(TAG, "OnLowMemory!!");
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
            case 3: mPager.setCurrentItem(0);
                    break;
        }
        mNavigation_drawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {

    	switch( mPager.getCurrentItem()) {

	    	case IMAGE_FRAGMENT: super.onBackPressed();
                    break;
            default:mPager.setCurrentItem(IMAGE_FRAGMENT);
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

    /**Sends path of the image to be opened to myImageViewer */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( resultCode == RESULT_CANCELED ) return;

        mImage_path = Utility.getRealPathFromURI(this, data.getData());
        Log.d(TAG, mImage_path);
        //final Mat mat = Highgui.imread(mImage_path);
        //mat.convertTo(mat, CvType.CV_8U);
        //Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2RGBA,4);
        //mCurrentBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        //Utils.matToBitmap(mat, mCurrentBitmap);

        mCurrentBitmap = BitmapFactory.decodeFile(mImage_path);
        mTempatePreview.setImageDrawable(mDefaultPreview);

//        if( mat.isContinuous()) {
//            Gdx.app.postRunnable(new Runnable() {
//                @Override
//                public void run() {
//                    byte[] buffer = new byte[(int) mat.total() * mat.channels()];
//                    mat.get(0, 0, buffer);
//                    Pixmap pixmap = new Pixmap(mat.width(), mat.height(), Pixmap.Format.RGBA8888);
//                    //pixmap.getPixels().put(buffer);
//                    Texture texture = new Texture(mat.width(),mat.height(), Pixmap.Format.RGBA8888);
//                    texture.draw(pixmap, 0, 0);
//                    pixmap.dispose();
//                    mCvInterface.OpenTexture(texture);
//                }
//            });
//        }
//        else
        //Utility.BitmapToTex(mCurrentBitmap,mCvInterface);

        mCvInterface.OpenImage(mImage_path);

        Log.d(TAG, "BitmapSize:" + mCurrentBitmap.getWidth() + "," + mCurrentBitmap.getHeight());
        FragmentFactory.getKeyboardFragment().refreshView();
    }

    @Override
    public void ConvertToText() {
        OpenCVModule.ConvertToText();
    }

    @Override
    public void StartSpotting() {

        //mWakeLock.acquire();
        Log.d(TAG, mCurrentTemplateRect.toString());
        Log.d(TAG,"Bitmap Size: "+mCurrentBitmap.getWidth()+","+mCurrentBitmap.getHeight());

        final UpdateViewCallback uvcallback = this;

        final Mat original = new Mat(),template = new Mat();
        Utils.bitmapToMat(mCurrentBitmap, original);
        Utils.bitmapToMat(mCurrentBitmapTemplate, template);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mWakeLock.acquire();
                Log.d(TAG, "[" + mPatchRows + "," + mPatchColumns + "," + mFragmentThreshold + "," + mMatchingThreshold + "]");
                OpenCVModule.SpotCharacters(original.clone(), template,
                        mPatchRows, mPatchColumns, mFragmentThreshold, mMatchingThreshold,
                        mUnicode, uvcallback);
                //Toast.makeText(this,"doing "+mPatchRows+mPatchColumns+"__"+mMatchingThreshold,Toast.LENGTH_SHORT).show();
                mWakeLock.release();
            }
        }).start();
//        BackgroundService.original = original;
//        BackgroundService.template = template;
//        BackgroundService.mFragmentThreshold = mFragmentThreshold;
//        BackgroundService.mMatchingThreshold = mMatchingThreshold;
//        BackgroundService.mPatchColumns = mPatchColumns;
//        BackgroundService.mPatchRows = mPatchRows;
//        BackgroundService.mUnicode = mUnicode;
//        BackgroundService.uvcallback = this;
//        BackgroundService.appContext = getApplicationContext();
//        Intent i = new Intent(getApplicationContext(),BackgroundService.class);
//        startService(i);
        FragmentFactory.getLibgdxFragment().ShowProgressBar();
    }

    @Override
    public void TemplateSelected(final int x, final int y, final int width, final int height, final String unicode) {

        Log.d(TAG, "Template Selected " + x + "," + y + "," + width + "," + height);
        mCurrentTemplateRect = new Rectangle(x,y,width,height);
        mCurrentBitmapTemplate = Bitmap.createBitmap(mCurrentBitmap,
                (int) mCurrentTemplateRect.getX(),(int) mCurrentTemplateRect.getY(),(int) mCurrentTemplateRect.getWidth(),(int) mCurrentTemplateRect.getHeight());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentFactory.getLibgdxFragment().setUnicodeText(unicode);
            }
        });
    }

    @Override
    public void TemplateMoved(int x, int y, int width, int height, String unicode) {
        TemplateSelected(x, y, width, height, unicode);
    }

    @Override
    public void TemplateResized(int x, int y, int width, int height, String unicode) {
        TemplateSelected(x, y, width, height, unicode);
    }

    @Override
    public void ShowKeyboard(CharSequence previewText) {
        FragmentFactory.getKeyboardFragment().setText(previewText);
        mPager.setCurrentItem(KEYBOARD_FRAGMENT);
    }

    @Override
    public void UnicodeEdited(String unicode) {
        FragmentFactory.getLibgdxFragment().setUnicodeText(unicode);
        mUnicode = unicode;
        mPager.setCurrentItem(IMAGE_FRAGMENT);
    }

    @Override
    public void ImageviewerReady(ControllerViewInterface cvInterface) {
        mCvInterface = cvInterface;
    }

    @Override
    public void KeyboardSelected(int keyboard_id) {
        FragmentFactory.getKeyboardFragment().setKeyboard(keyboard_id);
    }

    @Override
    public void PatchSizeChanged(int row_size, int col_size) {
        mPatchRows = row_size;
        mPatchColumns = col_size;
    }

    @Override
    public void UpdateProgress(int progress) {
        FragmentFactory.getLibgdxFragment().UpdateProgressBar(progress);
    }

    @Override
    public void SpottingUpdated(ArrayList<item> itemArrayList, String unicode, Mat image) {
        mCvInterface.SpottingUpdated(Utility.convertToVector(itemArrayList), unicode);
        //BackgroundService.SaveFile(mImage_path,image,mCurrentTemplateRect);
    }

    @Override
    public void ThresholdChanged(float mFragment_threshold, float mMatching_threshold) {

        mFragmentThreshold = mFragment_threshold;
        mMatchingThreshold = mMatching_threshold;
    }

    @Override
    public void SaveFile(String filename,Mat image) {

        Mat roi = image.submat(new Rect( (int)mCurrentTemplateRect.getX(),(int) mCurrentTemplateRect.getY(),
                (int) mCurrentTemplateRect.getWidth(), (int) mCurrentTemplateRect.getHeight() ));
        Mat color = new Mat(roi.size(),image.type(),new Scalar(150,0,0));
        double alpha = 0.35;
        Core.addWeighted(color, alpha, roi, 1.0 - alpha, 0.0, roi);
        image.convertTo(image, CvType.CV_8U);
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        filename = file2.toString();
        Highgui.imwrite(filename, image);
    }
    @Override
    public void onDrawerOpened(View drawerView) {
        if(mCurrentTemplateRect != null && mCurrentBitmap != null ) {
            mCurrentBitmapTemplate = Bitmap.createBitmap(mCurrentBitmap,
                    (int) mCurrentTemplateRect.getX(), (int) mCurrentTemplateRect.getY(), (int) mCurrentTemplateRect.getWidth(), (int) mCurrentTemplateRect.getHeight());
            mTempatePreview.setImageBitmap(mCurrentBitmapTemplate);
        }
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