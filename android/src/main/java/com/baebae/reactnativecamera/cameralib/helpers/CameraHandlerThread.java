package com.baebae.reactnativecamera.cameralib.helpers;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.baebae.reactnativecamera.cameralib.ui.CameraPreviewLayout;

// This code is mostly based on the top answer here: http://stackoverflow.com/questions/18149964/best-use-of-handlerthread-over-other-similar-classes
public class CameraHandlerThread extends HandlerThread {
    private static final String LOG_TAG = "CameraHandlerThread";

    private CameraPreviewLayout cameraPreviewLayout;

    public CameraHandlerThread(CameraPreviewLayout scannerView) {
        super("CameraHandlerThread");
        cameraPreviewLayout = scannerView;
        start();
    }

    public void startCamera(final Camera camera) {
        Handler localHandler = new Handler(getLooper());
        localHandler.post(new Runnable() {
            @Override
            public void run() {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cameraPreviewLayout.setupCameraPreview(camera);
                    }
                });
            }
        });
    }
}
