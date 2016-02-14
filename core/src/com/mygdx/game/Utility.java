package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

import java.nio.ByteBuffer;

/**
 * Created by monty on 2/14/2016.
 */
public abstract class Utility {

    /**
     * for generating different color based on unicode
     * @param value
     * @return distinct color based on value
     */

    public static Color Rainbow(double value)
    {
        double div = (Math.abs(value % 1) * 6);
        int ascending = (int) ((div % 1) * 255);
        int descending = 255 - ascending;

        switch ((int) div)
        {
            case 0:
                return new Color(255, ascending, 0, 255);
            case 1:
                return new Color(descending, 255, 0, 255);
            case 2:
                return new Color(0, 255, ascending ,255);
            case 3:
                return new Color(0, descending, 255 ,255);
            case 4:
                return new Color(ascending, 0, 255 ,255);
            default: // case 5:
                return new Color(255, 0, descending ,255);
        }
    }

    /**
     * converts unicode string into some long value
     * @param unicode 0-4 chars
     * @return long value
     */
    public static long UnicodetoLong(String unicode) {
        byte array[] = unicode.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getLong();
    }
}
