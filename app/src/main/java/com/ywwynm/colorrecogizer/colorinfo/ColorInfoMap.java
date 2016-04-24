package com.ywwynm.colorrecogizer.colorinfo;

import android.content.Context;

import com.ywwynm.colorrecogizer.R;
import com.ywwynm.colorrecogizer.utils.ColorUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by ywwynm on 2015/12/13.
 * Store some color informations in a {@link HashMap} and provide methods to find
 * the closest color for a given color from the map.
 */
public class ColorInfoMap {

    private static ColorInfoMap sColorInfoMap;

    // Index Access
    private HashMap<String, ColorInfo> mColorInfoMap;

    public static ColorInfoMap getInstance(Context context) {
        if (sColorInfoMap == null) {
            synchronized (ColorInfoMap.class) {
                if (sColorInfoMap == null) {
                    sColorInfoMap = new ColorInfoMap(context);
                }
            }
        }
        return sColorInfoMap;
    }

    private ColorInfoMap(Context context) {
        mColorInfoMap = new HashMap<>();
        readColorInfosFromFile(context);
    }

    public ColorInfo recognizeByHex(String hex) {
        ColorInfo colorInfo = mColorInfoMap.get(hex);
        if (colorInfo == null) {
            colorInfo = recognizeByHexRoughly(hex);
        }
        return colorInfo;
    }

    public ColorInfo recognizeByColor(int color) {
        return recognizeByHex(ColorUtils.colorToHexWithoutAlpha(color));
    }

    private void readColorInfosFromFile(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.colorinfo);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                ColorInfo colorInfo = generateColorInfo(line);
                mColorInfoMap.put(colorInfo.getHex(), colorInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ColorInfo generateColorInfo(String str) {
        String[] values = str.split(" ");
        String nameCN = values[0];
        String nameEN = values[1].replaceAll("-", " ");
        String hex    = values[2];
        String rgb    = values[3];
        String cmyk   = values[4];
        String hsv    = values[5];
        return new ColorInfo(nameCN, nameEN, hex, rgb, cmyk, hsv);
    }

    /**
     * find closest color for a given color from {@link #mColorInfoMap} by
     * calculating distance between two colors.
     */
    private ColorInfo recognizeByHexRoughly(String hex) {
        int[] rgb1 = ColorUtils.hexToRGB(hex);
        ColorInfo res = null;
        double minDistance = Double.MAX_VALUE;
        for (ColorInfo colorInfo : mColorInfoMap.values()) {
            int[] rgb2 = colorInfo.getRGBvalue();
            double distance = ColorUtils.calculateDistance(rgb1, rgb2);
            if (distance < minDistance) {
                minDistance = distance;
                res = colorInfo;
            }
        }
        return res;
    }

}
