package byr.criminalintent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeCameraFragment extends Fragment {

    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "byr.criminalintent.photo_filename";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    private String mExternalStoragePath = "/criminal_intent_camera";

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            //显示progressbar
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream out = null;
            boolean success = true;
            //外部存储 获取外部存储设备（SD卡）的路径
            File sdCardDictionary = Environment.getExternalStorageDirectory();
            File sdCardFile = new File(sdCardDictionary + mExternalStoragePath + "/" + fileName);
            FileOutputStream sdOut = null;
            //如果文件目录不存在，则创建目录
            if (!sdCardFile.getParentFile().exists()) {
                sdCardFile.getParentFile().mkdirs();
            }
            try {
                sdOut = new FileOutputStream(sdCardFile);
                sdOut.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file" + fileName, e);
                success = false;
            } finally {
                try {
                    if (sdOut != null) {
                        sdOut.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file" + fileName, e);
                    success = false;
                }
            }
            if (success) {
                Log.e(TAG, "JPEG saved at " + fileName);
                //将文件名回传给CrimePageActivity!
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, fileName);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

    public CrimeCameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //setPreviewDisplay
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera != null) {
                    //Surface大小改变时，改变camera preview大小，改变图片尺寸大小
                    Camera.Parameters parameters = mCamera.getParameters();
                    Camera.Size s = getBestSupportSize(parameters.getSupportedPreviewSizes(), width, height);
                    parameters.setPreviewSize(s.width, s.height);
                    s = getBestSupportSize(parameters.getSupportedPictureSizes(), width, height);
                    parameters.setPictureSize(s.width, s.height);
                    mCamera.setParameters(parameters);
                    try {
                        mCamera.startPreview();
                    } catch (Exception e) {
                        Log.e(TAG, "Could not start preview", e);
                        mCamera.release();
                        mCamera = null;
                    }
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });
        return v;
    }

    /**
     * 不优雅的计算最佳surface预览尺寸
     */
    private Camera.Size getBestSupportSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    //保证用户能够同fragment视图交互时，相机才可使用
    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open(0);
    }

    //及时释放资源
    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
