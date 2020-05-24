package com.e.ewhazp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.e.ewhazp.facedetector.SetDrowsinessState;
import com.google.firebase.samples.apps.mlkit.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;


public class InitPersonalSetting extends AppCompatActivity {
    private static TextToSpeech tts;
    Elements contents;
    Document doc = null;
    Date date = new Date();
    SimpleDateFormat nowdate = new SimpleDateFormat("yyyyMMdd");
    final String nowdate_s=nowdate.format(date);
    final String url = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid2=269&sid1=100&date="+nowdate_s;
    private List<String> plz = new ArrayList<>();
    static SetDrowsinessState sds = new SetDrowsinessState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Button tbtn = findViewById(R.id.testingbtn);
        tbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != ERROR) {
                            // 언어를 선택한다.
                            tts.setLanguage(Locale.KOREAN);
                        }
                    }
                });
                newstts();
            }
        });

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.settings, new SettingsFragment())
//                .commit();
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }
    public void newstts(){
        //Log.e(TAG,"newstts");
        //음성 알림: 많이 졸리신가요? 오늘 사람들이 가장 많이 본 뉴스를 읽어드릴게요.
        //뉴스 읽어주기 시작, 중간에 트리거 넣어서 중단될 수 있도록
        /*tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });*/
        String starting_annae = "많이 졸리신가요? 오늘 올라온 정치기사를 읽어드릴게요.";
        tts.speak(starting_annae,TextToSpeech.QUEUE_ADD, null);
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
                        tts.speak(plz.get(i), TextToSpeech.QUEUE_ADD, null);
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

//    public static class SettingsFragment extends PreferenceFragmentCompat {
//        @Override
//        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//        }
//    }

}