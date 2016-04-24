package com.ywwynm.colorrecogizer.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ywwynm.colorrecogizer.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ywwynm on 2015/12/13.
 * General utils
 */
public class GeneralUtils {

    private GeneralUtils() {}

    public static boolean hasLollipopAPI() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void decideTextViewColor(TextView textView, int backgroundColor) {
        Context context = textView.getContext();
        int textColor = ContextCompat.getColor(context, R.color.text_white);
        if (ColorUtils.isBrightColor(backgroundColor)) {
            textColor = ContextCompat.getColor(context, R.color.text_black);
        }
        textView.setTextColor(textColor);
    }

    /**
     * Set backgroundTint to {@link View} across all targeting platform level.
     * @param view the {@link View} to tint.
     * @param color color used to tint.
     */
    public static void tintView(View view, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(view.getBackground().mutate());
        DrawableCompat.setTint(wrappedDrawable, color);
        view.setBackground(wrappedDrawable);
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        view.clearFocus();

        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showWarningSnackbar(View view, @StringRes int textRes) {
        Snackbar.make(view, view.getContext().getString(textRes), Snackbar.LENGTH_SHORT).show();
    }

    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @SuppressWarnings("deprecation")
    public static byte[] getNV21BytesInJPEG(byte[] nv21Bytes, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        YuvImage yuvimage = new YuvImage(nv21Bytes, ImageFormat.NV21, size.width, size.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, baos);
        byte[] res = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
