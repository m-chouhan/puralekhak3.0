package com.mygdx.game;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

    /**Converts the templates(item) into gui suitable format
     * @param templates: templates
     * */
    public static ArrayList<Rectangle> convertToVector(ArrayList<item> templates) {

        ArrayList<Rectangle> list = new ArrayList<Rectangle>();
        for(item p : templates) {
            Rectangle rect = new Rectangle((float) (p.gety()-p.getposy()/2), (float) (p.getx()-p.getposx()/2),
                    (float) p.getposy(), (float) p.getposx());
            list.add(rect);
        }
        return list;
    }

    public static Texture BitmapToTex(final Bitmap bitmap, final ControllerViewInterface cvInterface) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                //bitmap.getByteCount();
                int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                for (int i = 0; i< pixels.length; i++) {
                    int pixel = pixels[i];
                    pixels[i] = (pixel << 8) | ((pixel >> 24) & 0xFF);
                }
                Pixmap pixmap = new Pixmap(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
                pixmap.getPixels().asIntBuffer().put(pixels);
                Texture texture = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
                texture.draw(pixmap, 0, 0);
                pixmap.dispose();
//                Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                cvInterface.OpenTexture(texture);
                //bitmap.recycle();
            }
        });
        return null;
    }
}