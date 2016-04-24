package com.ywwynm.colorrecogizer.utils;

import android.graphics.Color;

/**
 * Created by ywwynm on 2015/12/13.
 * utils for color.
 */
public class ColorUtils {

    private ColorUtils() {}

    public static int[] colorToRGB(int color) {
        int red   = Color.red(color);
        int green = Color.green(color);
        int blue  = Color.blue(color);
        return new int[] { red, green, blue };
    }

    public static int[] hexToRGB(String hex) {
        int color = Color.parseColor(hex);
        return colorToRGB(color);
    }

    public static String colorToHexWithoutAlpha(int color) {
        int[] rgb = colorToRGB(color);
        String r = Integer.toHexString(rgb[0]);
        String g = Integer.toHexString(rgb[1]);
        String b = Integer.toHexString(rgb[2]);
        if (r.length() == 1) {
            r = "0" + r;
        }
        if (g.length() == 1) {
            g = "0" + g;
        }
        if (b.length() == 1) {
            b = "0" + b;
        }
        return "#" + r + g + b;
    }

//    public static double calculateDistance(int[] rgb1, int[] rgb2) {
//        double redSquare   = Math.pow((rgb1[0] - rgb2[0]) * 0.299d, 2);
//        double greenSquare = Math.pow((rgb1[1] - rgb2[1]) * 0.587d, 2);
//        double blueSquare  = Math.pow((rgb1[2] - rgb2[2]) * 0.114d, 2);
//        return redSquare + greenSquare + blueSquare;
//    }

    /**
     * Calculate distance between two colors.
     * {@see http://www.w3.org/WAI/ER/WD-AERT/#color-contrast} for details.
     * @param rgb1 first color's rgb
     * @param rgb2 second color's rgb
     * @return distance between two colors
     */
    public static int calculateDistance(int[] rgb1, int[] rgb2) {
        int rd = Math.max(rgb1[0], rgb2[0]) - Math.min(rgb1[0], rgb2[0]);
        int rg = Math.max(rgb1[1], rgb2[1]) - Math.min(rgb1[1], rgb2[1]);
        int rb = Math.max(rgb1[2], rgb2[2]) - Math.min(rgb1[2], rgb2[2]);
        return rd + rg + rb;
    }

    /**
     * Judge a color is bright or dark.
     * {@see http://www.w3.org/WAI/ER/WD-AERT/#color-contrast} for details.
     * @param color color to judge if is bright
     * @return {@code true} if color is bright, {@code false} otherwise.
     */
    public static boolean isBrightColor(int color) {
        int[] rgb = colorToRGB(color);
        float judge = ((rgb[0] * 299) + (rgb[1] * 587) + (rgb[2] * 114)) / 1000f;
        return judge >= 125;
    }

}
