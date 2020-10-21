package com.bh.fittingsimulator.callback;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;


import com.bh.fittingsimulator.CalibrationActivity;

public class MySessionCallback extends CameraCaptureSession.CaptureCallback {
    CalibrationActivity calibrationActivity;

    public MySessionCallback(CalibrationActivity calibrationActivity){
        this.calibrationActivity = calibrationActivity;
    }
    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        calibrationActivity.mSession = session;
        unlockFocus();
    }

    @Override
    public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
        calibrationActivity.mSession = session;
    }

    @Override
    public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
        super.onCaptureFailed(session, request, failure);
    }
    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            calibrationActivity.mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            calibrationActivity.mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            calibrationActivity.mSession.capture(calibrationActivity.mPreviewBuilder.build(),  this,
                    calibrationActivity.mHandler);
            // After this, the camera will go back to the normal state of preview.
            calibrationActivity.mSession.setRepeatingRequest(calibrationActivity.mPreviewBuilder.build(), this,
                    calibrationActivity.mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}