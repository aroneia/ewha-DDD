package com.e.ewhazp.facedetector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ewhazp.ChooserActivity;
import com.e.ewhazp.preprocessing.CameraSource;
import com.e.ewhazp.preprocessing.CameraSourcePreview;
import com.e.ewhazp.preprocessing.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DetectingDrowsiness extends AppCompatActivity //가장 오래 사용될 화면
        implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "DetectingDrowsiness";
    private static final int PERMISSION_REQUESTS = 1;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

//    ServiceConnection MDtimer = new ServiceConnection() {
//        boolean isServicing = false;
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };
    public Button gotoInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detecting_drowsiness);

        preview = findViewById(R.id.firePreview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
        if (allPermissionsGranted()) {
            createCameraSource();
            startCameraSource();
        } else {
            getRuntimePermissions();
        }
        final Intent MDintent = new Intent(this, MonitorDrowsiness.class);

        startService(MDintent);
        Log.e(TAG,"ㅋㅋ...");

        gotoInit = findViewById(R.id.GotoInitpage);
        gotoInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(count1sisRunning) count1s.cancel();
//                if(count3sisRunning) count3s.cancel();
//                timer.cancel();
                stopService(MDintent);
                startActivity(new Intent(getApplicationContext(), ChooserActivity.class));
                finish();
            }
        });
    }



    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        Log.i(TAG, "Using Face Detector Processor");
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor());
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return false;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return true;
    }
}
