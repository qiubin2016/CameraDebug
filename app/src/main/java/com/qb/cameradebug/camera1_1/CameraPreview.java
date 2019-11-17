package com.qb.cameradebug.camera1_1;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private int displayOrientation = 0;
    private static int myCameraId = 0;

    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    private static Camera getCameraInstance() {
        Camera c = null;
        int cameraId = -1;
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CAMERA_FACING_FRONT) {
                    cameraId = i;
                    myCameraId = i;
                    break;
                }
                Log.i(TAG, "nums:"+Camera.getNumberOfCameras()+",id:"+i+",facing:"+cameraInfo.facing+",cameraId:"+cameraId);
            }
            Log.i(TAG, "cameraId:" + cameraId);
            c = Camera.open(cameraId);
        } catch (Exception e) {
            Log.d(TAG, "camera is not available");
        }
        return c;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = getCameraInstance();
        try {
            if (null == mCamera) {
                Log.i(TAG, "mCamera is null!");
            } else {
                Log.i(TAG, "mCamera is not null!");
            }

            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Log.i(TAG, "onPreviewFrame!size:" + data.length + ",width:" + camera.getParameters().getPreviewSize().width
                    + ",height:" + camera.getParameters().getPreviewSize().height);

                }
            });
            mCamera.setPreviewDisplay(holder);
            setCameraDisplayOrientation();
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    private int getCameraDisplayOrientation(int degrees, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation + degrees) % 360;
            rotation = (360 - rotation) % 360; // compensate the mirror
        } else { // back-facing
            rotation = (info.orientation - degrees + 360) % 360;
        }
        return rotation;
    }

    void setCameraDisplayOrientation() {
        int detectRotation = 0; // 人脸实际检测角度
        int cameraRotation = 0; // 摄像头图像预览角度
        int cameraFacing = CAMERA_FACING_FRONT;

        if (cameraFacing == CAMERA_FACING_FRONT) {
            cameraRotation = ORIENTATIONS.get(displayOrientation);
            cameraRotation = getCameraDisplayOrientation(cameraRotation, myCameraId, mCamera);
            mCamera.setDisplayOrientation(cameraRotation);
            detectRotation = cameraRotation;
            if (detectRotation == 90 || detectRotation == 270) {
                detectRotation = (detectRotation + 180) % 360;
            }
        } else if (cameraFacing == CAMERA_FACING_BACK) {
            cameraRotation = ORIENTATIONS.get(displayOrientation);
            cameraRotation = getCameraDisplayOrientation(cameraRotation, myCameraId, mCamera);
            mCamera.setDisplayOrientation(cameraRotation);
            detectRotation = cameraRotation;
        } /*else if (cameraFacing == CAMERA_USB) {
            mCamera.setDisplayOrientation(0);
            detectRotation = 0;
        }*/
        Log.i(TAG, "detectRotation:" + detectRotation + ",cameraRotation:" + cameraRotation);
        if (cameraRotation == 90 || cameraRotation == 270) {
            // 旋转90度或者270，需要调整宽高
//            mTextureView.setPreviewSize(previewHeight, previewWidth);
        } else {
//            mTextureView.setRotationY(180); // TextureView旋转90度
//            mTextureView.setPreviewSize(previewWidth, previewHeight);
        }
    }
}
