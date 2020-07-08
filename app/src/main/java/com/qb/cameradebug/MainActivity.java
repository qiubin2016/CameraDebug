package com.qb.cameradebug;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qb.cameradebug.camera1_1.Camera1PreviewActivity;
import com.qb.cameradebug.camera1_2.Camera1SampleActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private static final String TAG = "MainActivity";
    private final int CAMERA_REQUEST_CODE = 1;
    private Switch mSwitchFacing;    //切换前后置摄像头
    private Button mGetCameraParamButton;    //查看摄像头参数
    private Button mTestButton;    //测试按钮
    private Button mCamera1PreviewButton;    //Camera1预览按钮
    private Button mRequestPermissionButton;  //申请相机权限按钮
    private Button mCamera1SampleButton;    //camera1sample例程
    private TextView mLogTextView;           //显示日志的文本框
    private int mCount = 0;            //计数

    private int mFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    @Nullable
    private Camera.CameraInfo mCameraInfo = null;
    private int mCameraId = -1;

    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initView();
    }

    private void initView() {
        mSwitchFacing = findViewById(R.id.switchFacing);
        mSwitchFacing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //TODO
                    Log.i(TAG, "switchFacing is Checked!");
                    mFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }else {
                    //TODO
                    Log.i(TAG, "switchFacing is not Check!");
                    mFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
            }
        });
        mGetCameraParamButton = findViewById(R.id.getCameraParamButton);
        mGetCameraParamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Log.i(TAG, "getCameraParamButton onClick!");
                getCameraParam();
            }
        });
        mTestButton = findViewById(R.id.testButton);
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试按钮
                Log.i(TAG, "testButton onClick!");
                test();
            }
        });
        mCamera1PreviewButton = findViewById(R.id.camera1PreviewButton);
        mCamera1PreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera1Preview按钮
                Log.i(TAG, "camera1PreviewButton onClick!");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Camera1PreviewActivity.class);
                startActivity(intent);
            }
        });
        mRequestPermissionButton = findViewById(R.id.requestPermissionButton);
        mRequestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请相机权限按钮
                Log.i(TAG, "requesePermissionButton onClick!");
                requestPerssion();
            }
        });
        mCamera1SampleButton = findViewById(R.id.camera1SampleButton);
        mCamera1SampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera1Sample按钮
                Log.i(TAG, "camera1SampleButton onClick!");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Camera1SampleActivity.class);
                startActivity(intent);
            }
        });
        mLogTextView = findViewById(R.id.logTextView);
        mLogTextView.setText("");
        mLogTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mLogTextView.setScrollbarFadingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        // 动态权限检查
//        if (!isRequiredPermissionsGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
//        }
    }

    /**
     * 判断我们需要的权限是否被授予，只要有一个没有授权，我们都会返回 false。
     *
     * @return true 权限都被授权
     */
    private boolean isRequiredPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化摄像头信息。
     */
    private void initCameraInfo(int facing) {
        int numberOfCameras = Camera.getNumberOfCameras();// 获取摄像头个数
        Log.i(TAG, "numberOfCameras:" + numberOfCameras);
        for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            Log.i(TAG, "camera facing:" + cameraInfo.facing);
            if (cameraInfo.facing == facing) {  // 摄像头信息
                mCameraId = cameraId;
                mCameraInfo = cameraInfo;
                Log.i(TAG, "cameraId:" + mCameraId);
                break;
            }
        }
    }

    /**
     * 获取摄像头参数
     */
    private void getCameraParam() {
        // 动态权限检查
        if (!isRequiredPermissionsGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        }

        initCameraInfo(mFacing);  //获取摄像头id
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "open camera ...");
            Camera camera = Camera.open(mCameraId);  //打开摄像头
            if (null != camera) {  //摄像头是否打开成功
                Log.i(TAG, "open camera success!");
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                if (null != supportedPreviewSizes) {
                    for (Camera.Size previewSize : supportedPreviewSizes) {
                        Log.i(TAG, "supportedPreviewSizes height:" + previewSize.height + ",width" + previewSize.width);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedPreviewSizes");
                }
                //查看支持的预览尺寸
                List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
                if (null != supportedPreviewFormats) {
                    for (Integer previewFormats : supportedPreviewFormats) {
                        Log.i(TAG, "supportedPreviewFormats:" + previewFormats);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedPreviewFormats");
                }
                //查看支持的图片尺寸
                List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
                if (null != supportedPictureSizes) {
                    for (Camera.Size pictureSize : supportedPictureSizes) {
                        Log.i(TAG, "SupportedPictureSizes height:" + pictureSize.height + ",width:" + pictureSize.width);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedPictureSizes");
                }
                //查看支持的图片格式
                List<Integer> supportedPictureFormats = parameters.getSupportedPictureFormats();
                if (null != supportedPictureFormats) {
                    for (Integer pictureFormat : supportedPictureFormats) {
                        Log.i(TAG, "supportedPictureFormats:" + pictureFormat);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedPictureFormats");
                }
                //查看支持的视频尺寸
                List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
                if (null != supportedVideoSizes){
                    for (Camera.Size videoSize : supportedVideoSizes) {
                        Log.i(TAG, "SupportedVideoSizes height:" + videoSize.height + ",width:" + videoSize.width);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedVideoSizes");
                }
                //查看支持的白平衡
                List<String> supportedWhiteBalance = parameters.getSupportedWhiteBalance();
                if (null != supportedWhiteBalance) {
                    for (String whiteBalance : supportedWhiteBalance) {
                        Log.i(TAG, "supportedWhiteBalance:" + whiteBalance);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedWhiteBalance");
                }
                //查看支持的颜色效果
                List<String> supportedColorEffects = parameters.getSupportedColorEffects();
                if (null != supportedColorEffects) {
                    for (String colorEffect : supportedColorEffects) {
                        Log.i(TAG, "supportedColorEffects:" + colorEffect);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedColorEffects");
                }
                //查看支持的supportedAntibanding
                List<String> supportedAntibanding = parameters.getSupportedAntibanding();
                if (null != supportedAntibanding) {
                    for (String antibanding : supportedAntibanding) {
                        Log.i(TAG, "supportedAntibanding:" + antibanding);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedAntibanding");
                }
                //查看支持的场景模式
                List<String> supportedSceneModes = parameters.getSupportedSceneModes();
                if (null != supportedSceneModes) {
                    for (String sceneMode : supportedSceneModes) {
                        Log.i(TAG, "supportedSceneModes:" + sceneMode);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedSceneModes");
                }
                //查看支持的闪光灯模式
                List<String> supportedFlashModes = parameters.getSupportedFlashModes();
                if (null != supportedFlashModes) {
                    for (String flashMode : supportedFlashModes) {
                        Log.i(TAG, "supportedFlashModes:" + flashMode);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedFlashModes");
                }
                //查看支持的对焦模式
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (null != supportedFocusModes) {
                    for (String focusMode : supportedFocusModes) {
                        Log.i(TAG, "supportedFocusModes:" + focusMode);
                    }
                } else {
                    Log.i(TAG, "not supported getSupportedFocusModes");
                }
                //查看支持的jpeg缩略图大小
                List<Camera.Size> supportedJpegThumbnailSizes = parameters.getSupportedJpegThumbnailSizes();
                if (null != supportedJpegThumbnailSizes) {
                    for (Camera.Size jpegThumbnailSize : supportedJpegThumbnailSizes) {
                        Log.i(TAG, "supportedJpegThumbnailSizes height:" + jpegThumbnailSize.height + ",width:" + jpegThumbnailSize.width);
                    }
                }else {
                    Log.i(TAG, "not supported getSupportedJpegThumbnailSizes");
                }
                //查看支持的预览帧速率
                List<Integer> supportedPreviewFrameRates = parameters.getSupportedPreviewFrameRates();
                if (null != supportedPreviewFrameRates) {
                    for (Integer previewFrameRate : supportedPreviewFrameRates) {
                        Log.i(TAG, "supportedPreviewFrameRates:" + previewFrameRate);
                    }
                } else {
                    Log.i(TAG, "not supported getSupportedPreviewFrameRates");
                }
                //查看预览帧率范围 getSupportedPreviewFpsRange
                List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
                if (null != supportedPreviewFpsRange) {
                    for (int[] previewFpsRange : supportedPreviewFpsRange) {
                        Log.i(TAG, "previewFpsRange:");
                        for (int previewFps : previewFpsRange) {
                            Log.i(TAG, "" + previewFps);
                        }
                    }
                } else {
                    Log.i(TAG, "not supported getSupportedPreviewFpsRange");
                }
                camera.release();  //关闭摄像头
            }
        }

    }

    private void test() {
//        List<String> testList = null;
//        for (String testListValue : testList) {
//            Log.i(TAG, "testListValue:" + testListValue);
//        }
        mLogTextView.append("[" + mCount + "]:--------!\n");
        int offset = mLogTextView.getLineCount() * mLogTextView.getLineHeight();
        if(offset > mLogTextView.getHeight()){
            mLogTextView.scrollTo(0,offset - mLogTextView.getHeight());
        }
        mCount++;
    }

    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.M)
    void requestPerssion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {  //判断当前应用有没有CAMERA权限，如果没有则进行申请
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage("申请相机权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //申请相机权限
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }
                        })
                        .show();
            }else {
                //申请相机权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        } else {
            Log.i(TAG, "相机权限已申请");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "相机权限已申请1");
            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
