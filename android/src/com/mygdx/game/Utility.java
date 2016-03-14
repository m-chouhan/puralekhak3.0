package com.mygdx.game;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;

/**
 * Created by monty on 1/17/2016.
 * Copied Code :P
 * Utility class to fetch real path from a uri
 * required to send real path to libgdx fragment
 */

public class Utility {

    public static String getRealPathFromURI(Context context, Uri uri) {
        String realPath = "";
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = Utility.getRealPathFromURI_BelowAPI11(context, uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = Utility.getRealPathFromURI_API11to18(context, uri);

            // SDK > 19 (Android 4.4)
        else
            realPath = Utility.getRealPathFromURI_API19(context, uri);
        return realPath;
    }
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static ArrayList<Rectangle> convertToVector(ArrayList<item> points,float imWidth,float imHeight) {

        ArrayList<Rectangle> list = new ArrayList<Rectangle>();
        for(item p : points) {
            Rectangle rect = new Rectangle((float) p.gety(), (float) p.getx(),
                    (float) p.getposy(), (float) p.getposx());
            //rect.setPosition(rect.getX()-rect.getWidth()/2,rect.getY()-rect.getHeight()-2);
            list.add(rect);
        }
        return list;
    }
}