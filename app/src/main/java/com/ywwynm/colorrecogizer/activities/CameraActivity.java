package com.ywwynm.colorrecogizer.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ywwynm.colorrecogizer.colorinfo.ColorInfoMap;
import com.ywwynm.colorrecogizer.R;
import com.ywwynm.colorrecogizer.colorinfo.ColorInfo;
import com.ywwynm.colorrecogizer.utils.ColorUtils;
import com.ywwynm.colorrecogizer.utils.GeneralUtils;
import com.ywwynm.colorrecogizer.views.CameraPreview;
import com.ywwynm.colorrecogizer.views.ColorInfoPresenter;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity {

    private FrameLayout mFlCameraPreview;
    private CardView mCardColorToRecognize;
    private TextView mTvColorToRecognizeHex;

    private LinearLayout mLlRecognizedColor;

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    private ColorInfoPresenter mColorInfoPresenter;

    private ColorInfoMap mColorInfoMap;

    private RecognizeColorTask mRecognizeColorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initMembers();
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUi();
        setEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void initMembers() {
        mColorInfoMap = ColorInfoMap.getInstance(getApplicationContext());
    }

    private void findViews() {
        mFlCameraPreview = (FrameLayout) findViewById(R.id.fl_camera_preview);
        mCardColorToRecognize = (CardView) findViewById(R.id.card_color_to_recognize);
        mTvColorToRecognizeHex = (TextView) findViewById(R.id.tv_color_to_recognize_hex);

        mLlRecognizedColor = (LinearLayout) findViewById(R.id.ll_recognized_color);

        mColorInfoPresenter = new ColorInfoPresenter.Builder()
                .setTvColorNameCN((TextView) findViewById(R.id.tv_color_name_cn))
                .setTvColorNameEN((TextView) findViewById(R.id.tv_color_name_en))
                .setTvColorHex((TextView) findViewById(R.id.tv_color_hex))
                .setTvColorRGB((TextView) findViewById(R.id.tv_color_rgb))
                .setTvColorHSV((TextView) findViewById(R.id.tv_color_hsv))
                .setTvColorCMYK((TextView) findViewById(R.id.tv_color_cmyk))
                .build();
    }

    private void startUi() {
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        mFlCameraPreview.removeAllViews();
        mFlCameraPreview.addView(mCameraPreview);
    }

    private void setEvents() {
        mCameraPreview.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (mRecognizeColorTask != null) {
                    switch (mRecognizeColorTask.getStatus()) {
                        case RUNNING:
                            return;
                        case PENDING:
                            mRecognizeColorTask.cancel(true);
                            break;
                    }
                }
                mRecognizeColorTask = new RecognizeColorTask(data, camera);
                mRecognizeColorTask.execute(mFlCameraPreview.getWidth(), mFlCameraPreview.getHeight());
            }
        });

        mFlCameraPreview.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        int viewHeight = mFlCameraPreview.getHeight();
                        int marginTop = (int) (viewHeight - viewHeight / 4.5f);
                        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams)
                                mCardColorToRecognize.getLayoutParams();
                        fl.setMargins(0, marginTop, 0, 0);
                        mCardColorToRecognize.requestLayout();
                        return true;
                    }
                });
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            GeneralUtils.showWarningSnackbar(mFlCameraPreview, R.string.warning_camera_not_available);
        }
        return c;
    }

    private int[] getRecognizingPixelPosition(int viewWidth, int viewHeight, Camera.Size previewSize) {
        final float ratio = previewSize.height / viewWidth;
        return new int[] { (int) (viewHeight / 2 * ratio), previewSize.height / 2 };
    }

    private void updateColorToRecognizeUi(int color) {
        mCardColorToRecognize.setCardBackgroundColor(color);
        GeneralUtils.decideTextViewColor(mTvColorToRecognizeHex, color);
        String hex = ColorUtils.colorToHexWithoutAlpha(color);
        mTvColorToRecognizeHex.setText(hex);
    }

    private void updateRecognizedColorUi(ColorInfo colorInfo) {
        int color = colorInfo.getColor();
        mLlRecognizedColor.setBackgroundColor(color);
        mColorInfoPresenter.updateColorInfo(colorInfo);
        mColorInfoPresenter.setTextColor(color);
        if (GeneralUtils.hasLollipopAPI()) {
            Window window = getWindow();
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }

    class RecognizeColorTask extends AsyncTask<Integer, Object, ColorInfo> {

        private byte[] mNV21Bytes;
        private Camera mCamera;
        private Camera.Size mCameraPreviewSize;
        private int mColorToRecognize;

        public RecognizeColorTask(byte[] NV21Bytes, Camera camera) {
            mNV21Bytes = NV21Bytes;
            mCamera = camera;
            mCameraPreviewSize = camera.getParameters().getPreviewSize();
        }

        @Override
        protected ColorInfo doInBackground(Integer... params) {
            byte[] bitmapData = GeneralUtils.getNV21BytesInJPEG(mNV21Bytes, mCamera);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
            int[] pos = getRecognizingPixelPosition(params[0], params[1], mCameraPreviewSize);
            mColorToRecognize = bitmap.getPixel(pos[0], pos[1]);
            return mColorInfoMap.recognizeByColor(mColorToRecognize);
        }

        @Override
        protected void onPostExecute(ColorInfo colorInfo) {
            updateColorToRecognizeUi(mColorToRecognize);
            updateRecognizedColorUi(colorInfo);
        }
    }

}
