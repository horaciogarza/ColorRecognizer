package com.ywwynm.colorrecogizer.colorinfo;

import android.graphics.Color;

/**
 * Created by 张启 on 2015/12/13.
 * Color information.
 */
public class ColorInfo {

    private String mNameCN;
    private String mNameEN;
    private String mHex;
    private String mRGB;
    private String mCMYK;
    private String mHSV;

    ColorInfo(String nameCN, String nameEN, String hex, String RGB, String CMYK, String HSV) {
        mNameCN = nameCN;
        mNameEN = nameEN;
        mHex = hex;
        mRGB = RGB;
        mCMYK = CMYK;
        mHSV = HSV;
    }

    public String getNameCN() {
        return mNameCN;
    }

    public String getNameEN() {
        return mNameEN;
    }

    public String getHex() {
        return mHex;
    }

    public String getRGB() {
        return mRGB;
    }

    public int[] getRGBvalue() {
        return getValue(mRGB);
    }

    public String getCMYK() {
        return mCMYK;
    }

    public int[] getCMYKvalue() {
        return getValue(mCMYK);
    }

    public String getHSV() {
        return mHSV;
    }

    public int[] getHSVvalue() {
        return getValue(mHSV);
    }

    public int getColor() {
        return Color.parseColor(mHex);
    }

    @Override
    public String toString() {
        return mNameCN + " " + mNameEN + " " + mHex + " " + mRGB + " "
                + mCMYK + " " + mHSV;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ColorInfo)) {
            return false;
        }
        return mHex.equals(((ColorInfo) obj).mHex);
    }

    private int[] getValue(String valueStr) {
        String[] value = valueStr.split(",");
        final int len = value.length;
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            res[i] = Integer.valueOf(value[i]);
        }
        return res;
    }
}
