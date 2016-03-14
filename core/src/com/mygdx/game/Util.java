package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.nio.ByteBuffer;

/**
 * Created by monty on 2/14/2016.
 */
public final class Util {

    private static final String TAG = "Util";
    private static final String[] indexcolors = new String[]{
            "#000000", "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
            "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87",
            "#5A0007", "#809693", "#FEFFE6", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80",
            "#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100",
            "#DDEFFF", "#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F",
            "#372101", "#FFB500", "#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
            "#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66",
            "#885578", "#FAD09F", "#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED", "#886F4C",

            "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375", "#A3C8C9", "#FF913F", "#938A81",
            "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757", "#C8A1A1", "#1E6E00",
            "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
            "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329",
            "#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72", "#6A3A4C",
            "#83AB58", "#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800",
            "#BF5650", "#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51",
            "#C895C5", "#320033", "#FF6832", "#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58"
    };

    public static Color ColorFromList(int value) {
        String colorstring = indexcolors[value%indexcolors.length];
        colorstring = colorstring.replace("#","");
        Gdx.app.log(TAG,colorstring+","+value+", length "+indexcolors.length
                +" ,"+(value % indexcolors.length));
        return Color.valueOf(colorstring);
    }
    /**
     * for generating different color based on unicode
     * @param value
     * @return distinct color based on value
     */
    public static Color Rainbow(double value)
    {
        double div = (Math.abs(value % 62)/62 * 6);
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
    public static int UnicodetoInteger(String unicode) {

        if( unicode.length() < 1) return 0;
        int value = unicode.hashCode();
        Gdx.app.log(TAG,""+value);
        return value;
    }
}
