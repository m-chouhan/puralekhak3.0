package com.mygdx.game;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Rectangle;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

import java.io.File;

/**
 * Created by monty on 4/8/2016.
 */
public class BackgroundService extends IntentService {


    public static int mPatchRows,mPatchColumns;
    public static float mMatchingThreshold,mFragmentThreshold;
    public static Mat original,template;
    public static String mUnicode;
    public static UpdateViewCallback uvcallback;
    public static Context appContext;

    private static String TAG = "BackgroundService";

    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        mWakeLock.acquire();
        /*initial values for starting loop */
        int prevPatchCol = mPatchColumns;
        float prevMatchThresh = mMatchingThreshold,prevfragThresh = mFragmentThreshold;

        for(;mPatchRows <= 4;++mPatchRows) {
            for (mPatchColumns = prevPatchCol;mPatchColumns <= 4; ++mPatchColumns) {
                for (mFragmentThreshold = prevfragThresh; mFragmentThreshold <= 0.31f; mFragmentThreshold += 0.05f) {
                    for(mMatchingThreshold = prevMatchThresh;mMatchingThreshold <= 0.751f;mMatchingThreshold += 0.05f) {
                        Log.d(TAG, "[" + mPatchRows + "," + mPatchColumns + "," + mFragmentThreshold + "," + mMatchingThreshold + "]");

                        OpenCVModule.SpotCharacters(original.clone(), template,
                                mPatchRows, mPatchColumns, mFragmentThreshold, mMatchingThreshold,
                                mUnicode, uvcallback);
                        Toast.makeText(this,"doing "+mPatchRows+mPatchColumns+"__"+mMatchingThreshold,Toast.LENGTH_SHORT).show();
//                        mWakeLock.release();
//                        return;
                    }
                    prevMatchThresh = 0.5f;
                }
                prevfragThresh = 0.15f;
            }
            prevPatchCol = 2;
        }
        mWakeLock.release();
        Log.d(TAG, "Finished!!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    public static void SaveFile(String mImage_path,Mat image,Rectangle mCurrentTemplateRect) {

        Long tsLong = (System.currentTimeMillis()/1000)%100;
        String ts = tsLong.toString();
        String filename = mPatchRows +"_"+ mPatchColumns +"_"+ mFragmentThreshold + "_" +mMatchingThreshold + "_" +
                "__"+ ts + mImage_path.substring(mImage_path.lastIndexOf("/")+1);
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
}
