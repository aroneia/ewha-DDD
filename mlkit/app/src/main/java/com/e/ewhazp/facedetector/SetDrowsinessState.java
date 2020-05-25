package com.e.ewhazp.facedetector;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;

import com.e.ewhazp.ChooserActivity;

//EAR 값을 통해 졸음 상태 분석
//운전자의 상태에 따라 알맞은 알람 기능 호출
public class SetDrowsinessState  {
    private static final String TAG = "SetDrowsinessState";
    static SetDrowsinessState sds = new SetDrowsinessState();
    static ChooserActivity cc =new ChooserActivity();

    static ProgressDialog pd;
    static TextToSpeech tts;
    static int state = 0;


    static double EAR = 0;
    static double openedEAR = 0;
    static double closedEAR = 0;
    public static void setEARvalue(double ear) {
        SetDrowsinessState.EAR = ear;
    }
    public static void setOpenedEAR(double openedEAR) {
        SetDrowsinessState.openedEAR = openedEAR;
    }
    public static void setClosedEAR(double closedEAR) {
        SetDrowsinessState.closedEAR = closedEAR;
    }
    public static int getState(){return state;}
    public static void reduceState(){state=-1;}

    public static void setState(final int st) {
        //noticetoDriver.ringingAlert();
        //noticetoDriver.randomCalling(pd, cc.tts);
        state = st;
        switch (state){
                case 2: //2단계 진입
                    noticetoDriver.ringingAlert(); //소리알람 울림
                    break;
                case 3:
                    noticetoDriver.ringingAlert();
                    CountDownTimer count10s = new CountDownTimer(10000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }
                        @Override
                        public void onFinish() {
                            noticetoDriver.newstts(tts);
                        }
                    };
                    count10s.start();
                        break;
                case 4:
                noticetoDriver.ringingAlert();
                noticetoDriver.randomCalling(pd,tts);
                break;
            default: //꼭 써놔야 하는지...
                break;
        }
    }
    //@SuppressLint("StaticFieldLeak")

}