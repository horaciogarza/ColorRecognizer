package com.ywwynm.colorrecogizer.views;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.ywwynm.colorrecogizer.colorinfo.ColorInfo;
import com.ywwynm.colorrecogizer.utils.GeneralUtils;

/**
 * Created by ywwynm on 2015/12/16.
 * presenter for ColorInfo
 */
public class ColorInfoPresenter {

    private TextView mTvColorNameCN;
    private TextView mTvColorNameEN;
    private TextView mTvColorHex;
    private TextView mTvColorRGB;
    private TextView mTvColorHSV;
    private TextView mTvColorCMYK;

    private ColorInfoPresenter() {}

    @SuppressLint("SetTextI18n")
    public void updateColorInfo(ColorInfo colorInfo) {
        if (mTvColorNameCN != null) {
            mTvColorNameCN.setText(colorInfo.getNameCN());
        }
        if (mTvColorNameEN != null) {
            mTvColorNameEN.setText(colorInfo.getNameEN());
        }
        if (mTvColorHex != null) {
            mTvColorHex.setText(colorInfo.getHex());
        }
        if (mTvColorRGB != null) {
            mTvColorRGB.setText("RGB:    " + colorInfo.getRGB().replaceAll(",", ", "));
        }
        if (mTvColorHSV != null) {
            mTvColorHSV.setText("HSV:    " + colorInfo.getHSV().replaceAll(",", ", "));
        }
        if (mTvColorCMYK != null) {
            mTvColorCMYK.setText("CMYK: " + colorInfo.getCMYK().replaceAll(",", ", "));
        }
    }

    public void setTextColor(int backgroundColor) {
        if (mTvColorNameCN != null) {
            GeneralUtils.decideTextViewColor(mTvColorNameCN, backgroundColor);
        }
        if (mTvColorNameEN != null) {
            GeneralUtils.decideTextViewColor(mTvColorNameEN, backgroundColor);
        }
        if (mTvColorHex != null) {
            GeneralUtils.decideTextViewColor(mTvColorHex, backgroundColor);
        }
        if (mTvColorRGB != null) {
            GeneralUtils.decideTextViewColor(mTvColorRGB, backgroundColor);
        }
        if (mTvColorHSV != null) {
            GeneralUtils.decideTextViewColor(mTvColorHSV, backgroundColor);
        }
        if (mTvColorCMYK != null) {
            GeneralUtils.decideTextViewColor(mTvColorCMYK, backgroundColor);
        }
    }

    public static class Builder {
        private ColorInfoPresenter presenter = new ColorInfoPresenter();

        public Builder setTvColorNameCN(TextView tvColorNameCN) {
            presenter.mTvColorNameCN = tvColorNameCN;
            presenter.mTvColorNameCN.getPaint().setFakeBoldText(true);
            return this;
        }

        public Builder setTvColorNameEN(TextView tvColorNameEN) {
            presenter.mTvColorNameEN = tvColorNameEN;
            return this;
        }

        public Builder setTvColorHex(TextView tvColorHex) {
            presenter.mTvColorHex = tvColorHex;
            return this;
        }

        public Builder setTvColorRGB(TextView tvColorRGB) {
            presenter.mTvColorRGB = tvColorRGB;
            return this;
        }

        public Builder setTvColorHSV(TextView tvColorHSV) {
            presenter.mTvColorHSV = tvColorHSV;
            return this;
        }

        public Builder setTvColorCMYK(TextView tvColorCMYK) {
            presenter.mTvColorCMYK = tvColorCMYK;
            return this;
        }

        public ColorInfoPresenter build() {
            return presenter;
        }
    }

}
