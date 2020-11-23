package com.bh.fittingsimulator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                /*if(f.exists()){
                    //카메라 왜곡보정 결과가 있는 경우
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //카메라 왜곡보정 결과가 없는 경우
                    Intent intent = new Intent(getApplicationContext(), CalibrationExplainActivity.class);
                    startActivity(intent);
                    finish();
                }*/

                //파일 유무를 확인-내부저장소에 저장
                File files = new File("/sdcard/FittingSimulator/modeling.txt");
                if(files.exists()==true) {
                    //파일 있을 때 - 메인으로 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //파일 없을때 - 캘리브레이션으로 이동
                    Intent intent = new Intent(getApplicationContext(), CalibrationExplainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2500);

    }

    protected void onPause(){
        super.onPause();
        finish();
    }
}
