package com.e.ewhazp.facedetector;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.e.ewhazp.ChooserActivity.mcontext;

public class noticetoDriver extends Activity {
    private static boolean isAlertRinging = false;

    static ProgressDialog pd;

    private static TextToSpeech tts;
    static Elements contents;
    static Document doc = null;
    static Date date = new Date();
    static SimpleDateFormat nowdate = new SimpleDateFormat("yyyyMMdd");
    final static String nowdate_s=nowdate.format(date);
    final static String url = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid2=269&sid1=100&date="+nowdate_s;
    private static List<String> plz = new ArrayList<>();

    noticetoDriver ntd = new noticetoDriver();
    private static final String TAG = "noticetoDriver";
    //알람 모아놓기
    public static void ringingAlert(){

        if(!isAlertRinging) { //현재 알람이 울리고 있지 않다면
            isAlertRinging = true;
            //소리알람 구현
            //
            //
            //
            Log.e(TAG,"ringringringring~~");
            CountDownTimer count10s = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    isAlertRinging = false;
                }
            }.start();
        }
        else{
            Log.e(TAG,"Alert is ringing and the request is rejected ");
        }
        Log.e(TAG, "ringingAlert");
        //사용자가 설정한 소리 알람 중 하나를 무작위로 재생
    }
    public static void randomCalling(final ProgressDialog pd){ //무작위로 전화하기 on일 때
        Log.e(TAG, "randomCalling");
        //IninPersonalSetting에서 정보 받아옴
        //음성 알림: 위험합니다! 15초 이후 등록된 연락처에서 무작위로 전화를 겁니다.
        String startcalling = "위험합니다! 15초 이후 등록된 연락처에서 무작위로 전화를 겁니다.";
        //15초 대기 시작 countdowntimer()
        //onFinish(): 50%의 확률로 전화걸기 or 꽝
        LoadContactsAyscn lca = new LoadContactsAyscn();
        lca.execute();



    }
   public static void newstts(final TextToSpeech tts){ //은진언니가 구현
        Log.e(TAG,"newstts");
        //음성 알림: 많이 졸리신가요? 오늘 사람들이 가장 많이 본 뉴스를 읽어드릴게요.
        //뉴스 읽어주기 시작, 중간에 트리거 넣어서 중단될 수 있도록

       String starting_annae = "많이 졸리신가요? 오늘 올라온 정치기사를 읽어드릴게요.";
       tts.speak(starting_annae,TextToSpeech.QUEUE_ADD, null);
       tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);
       new AsyncTask() {//AsyncTask객체 생성
           @Override
           protected Object doInBackground(Object[] params) {
               try {
                   doc = Jsoup.connect(url).get();
                   contents = doc.select("div[class=\"list_body newsflash_body\"]");
                   //doc = Jsoup.connect("https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=100&date=").get(); //naver페이지를 불러옴
                   for(Element e:contents.select("dt")){
                       if(e.className().equals("photo")){
                           continue;
                       }
                       System.out.println(e.text());
                       plz.add(e.text());
                   }
                   for(int i=0;i<plz.size();i++){
                       tts.speak(plz.get(i),TextToSpeech.QUEUE_ADD, null);
                       tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
               return null;
           }
           @Override
           protected void onPostExecute(Object o) {
               super.onPostExecute(o);
           }
       }.execute();
   }


    static class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

//            pd = ProgressDialog.show(mcontext, "Loading Contacts",
  //                  "Please Wait");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ArrayList<String> contacts = new ArrayList<String>();

            Cursor c = mcontext.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            while (c.moveToNext()) {


                String phNumber = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contacts.add(phNumber);

            }
            Collections.sort(contacts);
            c.close();

            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);

            //pd.cancel();

            final Intent callIntent = new Intent(Intent.ACTION_CALL);
            Random random = new Random();
            //random하게 전화걸기
            int rand = random.nextInt(contacts.size()-1)+1;
            callIntent.setData(Uri.parse("tel:" + contacts.get(rand)));
            if (ActivityCompat.checkSelfPermission(mcontext.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mcontext.startActivity(callIntent);
            if(pd!=null)
                pd.dismiss();

        }

    }
}
