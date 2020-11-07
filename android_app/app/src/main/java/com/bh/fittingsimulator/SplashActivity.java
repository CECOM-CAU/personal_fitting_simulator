package com.bh.fittingsimulator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //카메라 왜곡보정 결과가 있는 경우
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                //카메라 왜곡보정 결과가 없는 경우
                //Intent intent = new Intent(getApplicationContext(), CalibrationExplainActivity.class);

                startActivity(intent);
                finish();
            }
        }, 2500);

    }

    protected void onPause(){
        super.onPause();
        finish();
    }
}
