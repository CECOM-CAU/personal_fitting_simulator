package com.bh.fittingsimulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SelectClothesActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clothes);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼


        //레이아웃 변경 버튼 -input_btn -직접입력 페이지
        final Button input_btn=(Button) findViewById(R.id.input_button);
        input_btn.setSelected(!input_btn.isSelected());
        input_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_btn.setSelected(!input_btn.isSelected());
                changeView(1);
            }
        });

        //레이아웃 변경 -qr_btn -qr코드 페이지
        Button qr_btn=(Button) findViewById(R.id.qr_button);
        qr_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_btn.setSelected(!input_btn.isSelected());
                changeView(2);
            }
        });

        //확인 버튼 눌렀을때
        Button ok_btn=(Button) findViewById(R.id.ok_button);
        ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        /*Spinner spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(parent.getItemAtPosition(position));
            }
        });*/


    }
    //레이아웃 변경 - 직접입력, qr코드
    public void changeView(int index){
        LinearLayout input_frame=(LinearLayout)findViewById(R.id.input_frame);
        LinearLayout qr_frame=(LinearLayout)findViewById(R.id.qr_frame);
        switch (index){
            case 1:
                input_frame.setVisibility(LinearLayout.VISIBLE);
                qr_frame.setVisibility(LinearLayout.INVISIBLE);
                break;

            case 2:
                input_frame.setVisibility(LinearLayout.INVISIBLE);
                qr_frame.setVisibility(LinearLayout.VISIBLE);
                break;
        }
    }

    @Override //툴바 뒤로가기 설정
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
