package com.baebae.reactnativecamera;

import com.baebae.reactnativecamera.cameralib.helpers.CameraInstanceManager;
import com.baebae.reactnativecamera.cameralib.ui.CameraPreviewLayout;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.zxing.Result;

public class CameraView extends CameraPreviewLayout implements LifecycleEventListener{

    public CameraView(ThemedReactContext context, CameraInstanceManager cameraInstanceManager) {
        super(context, cameraInstanceManager);
        context.addLifecycleEventListener(this);
    }

    private final Runnable mLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(mLayoutRunnable);
    }

    @Override
    public void onHostResume() {
        startCamera();
    }

    @Override
    public void onHostPause() {
        stopCamera();
    }

    @Override
    public void onHostDestroy() {
        stopCamera();
    }

    @Override
    protected void onImageFileSaved(String imagePath) {
        super.onImageFileSaved(imagePath);
        WritableMap event = Arguments.createMap();
        event.putString("message", "file://" + imagePath);
        event.putString("type", "camera_capture");
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event
        );

        stopCamera();
        startCamera();
    }

    @Override
    protected void onBarcodeScanned(Result barcodeResult) {
        super.onBarcodeScanned(barcodeResult);
        WritableMap event = Arguments.createMap();

        WritableMap message = Arguments.createMap();
        message.putString("data", barcodeResult.getText());
        message.putString("type", barcodeResult.getBarcodeFormat().name());

        WritableMap bounds = Arguments.createMap();
        WritableMap origin = Arguments.createMap();
        origin.putDouble("x", barcodeResult.getResultPoints()[0].getX());
        origin.putDouble("y", barcodeResult.getResultPoints()[0].getY());

        WritableMap size = Arguments.createMap();
        size.putDouble("height", Math.abs(barcodeResult.getResultPoints()[0].getY() - barcodeResult.getResultPoints()[1].getY()));
        size.putDouble("width", Math.abs(barcodeResult.getResultPoints()[0].getX() - barcodeResult.getResultPoints()[1].getX()));

        bounds.putMap("origin", origin);
        bounds.putMap("size", size);

        message.putMap("bounds", bounds);
        event.putMap("message", message);
        event.putString("type", "barcode_capture");
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event
        );
    }
}
