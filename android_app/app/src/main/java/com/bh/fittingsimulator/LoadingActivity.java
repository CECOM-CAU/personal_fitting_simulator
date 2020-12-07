package com.bh.fittingsimulator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadingActivity extends AppCompatActivity {

    private String heightvalue = ""; //키

    private SaveVideoTask saveVideoTask;
    private int result=0;
    private String returnString0,returnString1, returnString2, returnString3, returnString4,returnString5,returnString6,returnString7,returnString8,returnString9;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        Intent intent = getIntent();
        heightvalue = intent.getStringExtra("height");

        //서버로 보내기
        try {
            result = new SaveVideoTask().execute().get();
            Log.v("result",Integer.toString(result));

            if(result==1){
                
                
                Intent sintent = new Intent(LoadingActivity.this, ModelingSuccessActivity.class);
                startActivity(sintent);
                finish();
                
                /*sintent.putExtra("s0",returnString0);
                sintent.putExtra("s1",returnString1);
                sintent.putExtra("s2",returnString2);
                sintent.putExtra("s3",returnString3);
                sintent.putExtra("s4",returnString4);
                sintent.putExtra("s5",returnString5);
                sintent.putExtra("s6",returnString6);
                sintent.putExtra("s7",returnString7);
                sintent.putExtra("s8",returnString8);
                sintent.putExtra("s9",returnString9);*/
            }
            else { //실패시
                Intent sintent = new Intent(LoadingActivity.this, ModelingFailActivity.class);
                startActivity(sintent);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class SaveVideoTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Integer doInBackground(Void... data) {

            Log.d("test", "messagte");

            Document doc;
            String sUrl = "http://165.194.44.20:5000/getModelData_test";
            String sUrl2 = "http://10.0.2.2:5000/";

            Elements element;
            Connection.Response res;
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FittingSimulator/modeling.mp4");
            Log.v("file", file.getPath());

            try {
                res = Jsoup.connect(sUrl).data("file", file.getName(), new FileInputStream(file)).data("height", heightvalue).method(Connection.Method.POST).execute();

                //값 받아오기
                element = res.parse().select("h1");
                returnString0 = element.get(0).text();
                returnString1 = element.get(1).text();
                returnString2 = element.get(2).text();
                returnString3 = element.get(3).text();
                returnString4 = element.get(4).text();
                returnString5 = element.get(5).text();
                returnString6 = element.get(6).text();
                returnString7 = element.get(7).text();
                returnString8 = element.get(8).text();
                returnString9 = element.get(9).text();
                Log.d("abc", returnString0);

            } catch (IOException e) {
                e.printStackTrace();
                return 0;

            }
            return 1;
        }

    }
}
