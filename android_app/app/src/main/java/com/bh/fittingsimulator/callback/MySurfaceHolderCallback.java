package com.bh.fittingsimulator.callback;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bh.fittingsimulator.CalibrationActivity;
import com.bh.fittingsimulator.SaveImageTask;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MySurfaceHolderCallback implements SurfaceHolder.Callback{
    CalibrationActivity calibrationActivity;
    public MySurfaceHolderCallback(CalibrationActivity calibrationActivity){
        this.calibrationActivity = calibrationActivity;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCameraAndPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @TargetApi(19)
    public void initCameraAndPreview() {
        HandlerThread handlerThread = new HandlerThread("CAMERA2");
        handlerThread.start();
        calibrationActivity.mHandler = new Handler(handlerThread.getLooper());
        Handler mainHandler = new Handler(calibrationActivity.getMainLooper());
        try {
            String mCameraId = "" + CameraCharacteristics.LENS_FACING_FRONT; // 후면 카메라 사용

            CameraManager mCameraManager = (CameraManager) calibrationActivity.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size largestPreviewSize = map.getOutputSizes(ImageFormat.JPEG)[0];
            Log.i("LargestSize", largestPreviewSize.getWidth() + " " + largestPreviewSize.getHeight());

            setAspectRatioTextureView(largestPreviewSize.getHeight(),largestPreviewSize.getWidth());
            calibrationActivity.mImageReader = ImageReader.newInstance(largestPreviewSize.getWidth(), largestPreviewSize.getHeight(), ImageFormat.JPEG,/*maxImages*/7);
            calibrationActivity.mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mainHandler);
            if (ActivityCompat.checkSelfPermission(calibrationActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCameraManager.openCamera(mCameraId, new CameraDeviceCallback(calibrationActivity)/*deviceStateCallback*/, calibrationActivity.mHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(calibrationActivity, "카메라를 열지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAspectRatioTextureView(int ResolutionWidth , int ResolutionHeight)
    {
        if(ResolutionWidth > ResolutionHeight){
            int newWidth = calibrationActivity.mDSI_width;
            int newHeight = ((calibrationActivity.mDSI_width * ResolutionWidth)/ResolutionHeight);
            updateTextureViewSize(newWidth,newHeight);

        }else {
            int newWidth =calibrationActivity. mDSI_width;
            int newHeight = ((calibrationActivity.mDSI_width * ResolutionHeight)/ResolutionWidth);
            updateTextureViewSize(newWidth,newHeight);
        }
    }
    private void updateTextureViewSize(int viewWidth, int viewHeight) {
        Log.d("@@@", "TextureView Width : " + viewWidth + " TextureView Height : " + viewHeight);
        calibrationActivity.mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(viewWidth, viewHeight));
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            try {
                new SaveImageTask(calibrationActivity).execute(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
