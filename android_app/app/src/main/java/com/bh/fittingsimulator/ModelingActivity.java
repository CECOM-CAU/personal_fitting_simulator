package com.bh.fittingsimulator;


import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class ModelingActivity extends AppCompatActivity{

    private ImageButton video_btn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modeling);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        video_btn=findViewById(R.id.videobutton);
        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비디오 촬영
                Toast.makeText(ModelingActivity.this, "비디오 촬영",Toast.LENGTH_SHORT).show();

                //성공한경우
                Intent intent=new Intent(ModelingActivity.this,ModelingSuccessActivity.class);
                startActivity(intent);

                /*실패한 경우
                Intent intent=new Intent(ModelingActivity.this,ModelingFailActivity.class);
                startActivity(intent);
                 */

            }
        });

    }


    //툴바 설정 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override //툴바 뒤로가기 설정
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.menu_calib:{
                Toast.makeText(this, "캘리브레이션 이동", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_modeling:{
                Toast.makeText(this, "모델링 이동", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
