package com.e.ewhazp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ewhazp.facedetector.SetDrowsinessState;
import com.e.ewhazp.facedetector.SetopenedearActivity;
import com.google.firebase.samples.apps.mlkit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.util.Log.ERROR;

/**
 * Demo app chooser which takes care of runtime permission requesting and allows you to pick from
 * all available testing Activities.
 */
public final class ChooserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemClickListener {
    private static final String TAG = "ChooserActivity";
    private static final int PERMISSION_REQUESTS = 1;
    public Button startButton;
    public Button firstmetBtn;
    //임의로 추가
    public Button checktts;
    public Button randomcall;

    public static Context mcontext;
    public static TextToSpeech tts;

    //class 목록
    private static final Class<?>[] CLASSES =
            //메인화면에서 기능 선택할 때 액티비티 클래스들을 여기다가 넣으면 됨
            new Class<?>[] {
                    InitPersonalSetting.class,
                    SetopenedearActivity.class,
                    SetDrowsinessState.class
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mcontext = this;
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start);
        firstmetBtn = (Button) findViewById(R.id.firstmet);
        //임의로 추가
        checktts = (Button) findViewById(R.id.checktts);
        randomcall=(Button)findViewById(R.id.randomcall);

       tts = new TextToSpeech(mcontext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }

        }); //이거 추가


        //처음 시작하는 사용자: 초기설정 세팅(소리 알람 종류, 무작위로 전화하기 연락처 선택, 정치 뉴스 tts기능 on/off)
        //InitPersonalSetting 실행
        firstmetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InitPersonalSetting 실행
                Class<?> clicked = CLASSES[0];
                startActivity(new Intent( getApplicationContext(), clicked));
            }
        });


        //설정이 완료된 사용자
        //SetopenedearActivity 실행
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[1];
                startActivity(new Intent( getApplicationContext(), clicked));
            }
        });

        //tts 확인 차
        checktts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[2];
                startActivity(new Intent(getApplicationContext(),clicked));
            }
        });

        //randomcalling 확인 차
        randomcall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Class<?> clicked = CLASSES[2];
                startActivity(new Intent(getApplicationContext(),clicked));
            }
        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
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