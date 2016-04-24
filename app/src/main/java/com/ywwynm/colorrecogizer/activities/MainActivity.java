package com.ywwynm.colorrecogizer.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ywwynm.colorrecogizer.colorinfo.ColorInfoMap;
import com.ywwynm.colorrecogizer.R;
import com.ywwynm.colorrecogizer.colorinfo.ColorInfo;
import com.ywwynm.colorrecogizer.utils.ColorUtils;
import com.ywwynm.colorrecogizer.utils.GeneralUtils;
import com.ywwynm.colorrecogizer.views.ColorInfoPresenter;

public class MainActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 600;

    private static final int ANIMATE_STATUS_BAR     = 0;
    private static final int ANIMATE_NAVIGATION_BAR = 1;

    private RelativeLayout mRlColorToRecognize;
    private RelativeLayout mRlRecognizedColor;

    private TextView mTvHexPrefix;
    private EditText mEtHex;
    private FloatingActionButton mFabRecognize;
    private FloatingActionButton mFabOpenCamera;

    private ColorInfoPresenter mColorInfoPresenter;

    private ColorInfoMap mColorInfoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMembers();
        findViews();
        initUi();
        setEvents();
    }

    private void initMembers() {
        mColorInfoMap = ColorInfoMap.getInstance(getApplicationContext());
    }

    private void findViews() {
        mRlColorToRecognize = (RelativeLayout) findViewById(R.id.rl_color_to_recognize);
        mRlRecognizedColor  = (RelativeLayout) findViewById(R.id.rl_recognized_color);

        mTvHexPrefix   = (TextView) findViewById(R.id.tv_hex_prefix);
        mEtHex         = (EditText) findViewById(R.id.et_color_to_recognize_hex);
        mFabRecognize  = (FloatingActionButton) findViewById(R.id.fab_recognize);
        mFabOpenCamera = (FloatingActionButton) findViewById(R.id.fab_open_camera);

        mColorInfoPresenter = new ColorInfoPresenter.Builder()
                .setTvColorNameCN((TextView) findViewById(R.id.tv_color_name_cn))
                .setTvColorNameEN((TextView) findViewById(R.id.tv_color_name_en))
                .setTvColorHex((TextView) findViewById(R.id.tv_color_hex))
                .setTvColorRGB((TextView) findViewById(R.id.tv_color_rgb))
                .setTvColorHSV((TextView) findViewById(R.id.tv_color_hsv))
                .setTvColorCMYK((TextView) findViewById(R.id.tv_color_cmyk))
                .build();
    }

    private void initUi() {
        if (GeneralUtils.hasLollipopAPI()) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        GeneralUtils.tintView(mEtHex, ContextCompat.getColor(this, R.color.hint_black));
    }

    private void setEvents() {
        mFabRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.hideKeyboard(getCurrentFocus());

                String hex = "#" + mEtHex.getText().toString();
                int colorToRecognize;
                try {
                    colorToRecognize = Color.parseColor(hex);
                } catch (IllegalArgumentException e) {
                    GeneralUtils.showWarningSnackbar(mRlRecognizedColor, R.string.warning_wrong_hex);
                    return;
                }

                updateTopUi(colorToRecognize);

                ColorInfo recognizedColor = mColorInfoMap.recognizeByHex(hex);
                updateBottomUi(recognizedColor);

                updateMiddleUi(colorToRecognize, recognizedColor.getColor());
            }
        });

        mFabOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GeneralUtils.hasCamera(getApplicationContext())) {
                    GeneralUtils.showWarningSnackbar(mRlRecognizedColor, R.string.warning_no_camera);
                } else {
                    final Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            }
        });

        mFabRecognize.setOnLongClickListener(new OnFabLongClickListener(R.string.act_recognize));
        mFabOpenCamera.setOnLongClickListener(new OnFabLongClickListener(R.string.act_open_camera));
    }

    private void updateTopUi(int color) {
        animateColorChange(mRlColorToRecognize, color);

        if (GeneralUtils.hasLollipopAPI()) {
            animateColorChange(ANIMATE_STATUS_BAR, color);
        }

        GeneralUtils.decideTextViewColor(mTvHexPrefix, color);
        GeneralUtils.decideTextViewColor(mEtHex, color);

        int hintColor = ContextCompat.getColor(this, R.color.hint_white);
        if (ColorUtils.isBrightColor(color)) {
            hintColor = ContextCompat.getColor(this, R.color.hint_black);
        }
        GeneralUtils.tintView(mEtHex, hintColor);

        String temp = mEtHex.getText().toString();
        mEtHex.setText("");
        mEtHex.setHintTextColor(hintColor);
        mEtHex.append(temp);
    }

    private void updateBottomUi(ColorInfo colorInfo) {
        updateBottomColors(colorInfo.getColor());
        mColorInfoPresenter.updateColorInfo(colorInfo);
    }

    private void updateBottomColors(int colorTop) {
        animateColorChange(mRlRecognizedColor, colorTop);

        if (GeneralUtils.hasLollipopAPI()) {
            animateColorChange(ANIMATE_NAVIGATION_BAR, colorTop);
        }

        mColorInfoPresenter.setTextColor(colorTop);
    }

    private void updateMiddleUi(int colorTop, int colorBottom) {
        int fabColor = Color.WHITE;
        if (ColorUtils.isBrightColor(colorTop)) {
            fabColor = Color.BLACK;
        }
        ColorStateList colorStateList = ColorStateList.valueOf(fabColor);
        mFabRecognize.setBackgroundTintList(colorStateList);
        mFabOpenCamera.setBackgroundTintList(colorStateList);

        mFabRecognize.getDrawable().mutate().setColorFilter(colorTop, PorterDuff.Mode.SRC_ATOP);
        mFabOpenCamera.getDrawable().mutate().setColorFilter(colorBottom, PorterDuff.Mode.SRC_ATOP);
    }

    private void animateColorChange(RelativeLayout rl, int endColor) {
        int startColor = ((ColorDrawable) rl.getBackground()).getColor();
        ObjectAnimator
                .ofObject(rl, "backgroundColor", new ArgbEvaluator(), startColor, endColor)
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    @SuppressLint("NewApi")
    private void animateColorChange(int type, int endColor) {
        ValueAnimator valueAnimator = null;
        final Window window = getWindow();
        if (type == ANIMATE_STATUS_BAR) {
            int startColor = ((ColorDrawable) mRlColorToRecognize.getBackground()).getColor();
            valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    window.setStatusBarColor((int) animation.getAnimatedValue());
                }
            });
        } else if (type == ANIMATE_NAVIGATION_BAR) {
            int startColor = ((ColorDrawable) mRlRecognizedColor.getBackground()).getColor();
            valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    window.setNavigationBarColor((int) animation.getAnimatedValue());
                }
            });
        }

        if (valueAnimator != null) {
            valueAnimator.setDuration(ANIMATION_DURATION);
            valueAnimator.start();
        }
    }

    class OnFabLongClickListener implements View.OnLongClickListener {

        int mTextRes;

        OnFabLongClickListener(int textRes) {
            mTextRes = textRes;
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(MainActivity.this, getString(mTextRes), Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
