package com.bh.fittingsimulator.callback;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bh.fittingsimulator.CalibrationActivity;

import java.util.Arrays;

public class CameraDeviceCallback extends CameraDevice.StateCallback {
        CalibrationActivity calibrationActivity;

        CameraDeviceCallback(CalibrationActivity calibrationActivity){
            this.calibrationActivity = calibrationActivity;
        }
        @Override
        public void onOpened(CameraDevice camera) {
            calibrationActivity.mCameraDevice = camera;
            try {
                takePreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (calibrationActivity.mCameraDevice != null) {
                calibrationActivity.mCameraDevice.close();
                calibrationActivity.mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Toast.makeText(calibrationActivity, "카메라를 열지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

    public void takePreview() throws CameraAccessException {
        calibrationActivity.mPreviewBuilder = calibrationActivity.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        calibrationActivity.mPreviewBuilder.addTarget(calibrationActivity.mSurfaceViewHolder.getSurface());
        calibrationActivity. mCameraDevice.createCaptureSession(Arrays.asList(calibrationActivity.mSurfaceViewHolder.getSurface(), calibrationActivity.mImageReader.getSurface()), mSessionPreviewStateCallback, calibrationActivity.mHandler);
    }

    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            calibrationActivity.mSession = session;
            try {
                calibrationActivity.mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                calibrationActivity.mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                calibrationActivity.mSession.setRepeatingRequest(calibrationActivity.mPreviewBuilder.build(), null, calibrationActivity.mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Toast.makeText(calibrationActivity, "카메라 구성 실패", Toast.LENGTH_SHORT).show();
        }
    };
}

