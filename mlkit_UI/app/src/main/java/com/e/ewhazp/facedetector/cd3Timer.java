package com.e.ewhazp.facedetector;

import android.os.CountDownTimer;
import android.util.Log;

import static com.e.ewhazp.facedetector.SetDrowsinessState.EAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.closedEAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.openedEAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.setState;
import static com.e.ewhazp.facedetector.SetDrowsinessState.state;


public class cd3Timer extends CountDownTimer {
    private static final String TAG = "cd3Timer";
    private boolean cd3locked = false;

    static private int counttimes = 0;
    private static boolean count3sisRunning = false;


    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */

    private cd3Timer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    private static class cd3TimerHolder{
        private static final cd3Timer HCD_3_TIMER = new cd3Timer(3000,30);
    }

    public static cd3Timer getInstancecd3(){
        return cd3TimerHolder.HCD_3_TIMER;
    }
    public boolean isCount3sisRunning(){
        return count3sisRunning;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        count3sisRunning = true;
        if(!((EAR<openedEAR)&&(EAR>closedEAR))){ //매 틱마다 검사
            count3sisRunning = false;
            cancel();
        }
    }

    @Override
    public void onFinish() {
        Log.e(TAG,"cd3Timer onFinish");
        setState(state);
        count3sisRunning = false;
    }
}