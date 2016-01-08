package com.baebae.reactnativecamera.cameralib.helpers;

import android.hardware.Camera;
import android.view.Surface;

public class CameraInstanceManager {

    private int cameraCount = Camera.getNumberOfCameras();
    private Camera[] cameraInstanceList = new Camera[cameraCount];

    public CameraInstanceManager() {

    }

    public Camera getCamera(int id) {
        Camera camera = null;
        if (id < cameraCount) {
            if (!(cameraInstanceList[id] instanceof Camera)) {
                cameraInstanceList[id] = Camera.open(id);
                camera = cameraInstanceList[id];
            } else {
                camera = cameraInstanceList[id];
            }
        }
        return camera;
    }

    public Camera getCamera(String name) {
        name = name.toLowerCase();
        int cameraId = 0;
        switch (name) {
            case "back": cameraId = 0; break;
            case "front": cameraId = 1; break;
        }
        for (int i = 0; i < cameraInstanceList.length; i++) {
            if (i != cameraId) {
                releaseCamera(cameraInstanceList[i]);
            }
        }
        return getCamera(cameraId);
    }

    public void releaseCamera(Camera camera) {
        if (camera == null) return;
        camera.release();
        int cameraId = getCameraId(camera);
        if (cameraId > -1) {
            cameraInstanceList[getCameraId(camera)] = null;
        }
    }

    public int getCameraId(Camera camera) {
        for (int i = 0; i < cameraInstanceList.length; i++) {
            if (cameraInstanceList[i] == camera) {
                return i;
            }
        }
        return -1;
    }
}
