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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectClothesActivity extends AppCompatActivity {

    private ArrayAdapter<CharSequence> adspin1,adspin2;

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
        final Button qr_btn=(Button) findViewById(R.id.qr_button);
        input_btn.setSelected(!input_btn.isSelected());
        input_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_btn.setSelected(!input_btn.isSelected());
                qr_btn.setSelected(!qr_btn.isSelected());
                changeView(1);
            }
        });

        //레이아웃 변경 -qr_btn -qr코드 페이지
        qr_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_btn.setSelected(!input_btn.isSelected());
                qr_btn.setSelected(!qr_btn.isSelected());
                changeView(2);
            }
        });

        //상의-확인 버튼 눌렀을때
        Button top_ok_btn=(Button) findViewById(R.id.top_ok_button);
        top_ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //메인화면으로
                Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                startActivity(intent);

                EditText top_shoulder=(EditText)findViewById(R.id.top_shoulder_et);//상의_어깨길이
                Double.parseDouble(top_shoulder.getText().toString());//더블형
                //Integer.parseInt(top_shoulder.getText().toString());//정수형

                EditText top_arm=(EditText)findViewById(R.id.top_arm_et);//상의_팔길이
                Double.parseDouble(top_arm.getText().toString());//더블형
                //Integer.parseInt(top_arm.getText().toString());//정수형

                EditText top_chest=(EditText)findViewById(R.id.top_chest_et);//상의_가슴단면
                Double.parseDouble(top_chest.getText().toString());//더블형
                //Integer.parseInt(top_chest.getText().toString());//정수형

                EditText top_arm_width=(EditText)findViewById(R.id.top_arm_width_et);//상의_소매폭
                Double.parseDouble(top_arm_width.getText().toString());//더블형
                //Integer.parseInt(top_arm_width.getText().toString());//정수형

                EditText top_total_len=(EditText)findViewById(R.id.top_total_len_et);//상의_총길이
                Double.parseDouble(top_total_len.getText().toString());//더블형
                //Integer.parseInt(top_total_len.getText().toString());//정수형
            }
        });
        //바지-확인 버튼 눌렀을때
        Button pants_ok_btn=(Button) findViewById(R.id.pants_ok_button);
        pants_ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //메인화면으로
                Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
        //치마-확인 버튼 눌렀을때
        Button skirt_ok_btn=(Button) findViewById(R.id.skirt_ok_button);
        skirt_ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //메인화면으로
                Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
        //원피스-확인 버튼 눌렀을때
        Button dress_ok_btn=(Button) findViewById(R.id.dress_ok_button);
        dress_ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //메인화면으로
                Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        });


        //옷 카테고리에서 선택했을 때 해당하는 프레임 보여주기
        final View top_frame=findViewById(R.id.top_frame);
        final View pants_frame=findViewById(R.id.pants_frame);
        final View skirt_frame=findViewById(R.id.skirt_frame);
        final View dress_frame=findViewById(R.id.dress_frame);

        top_frame.setVisibility(View.VISIBLE);
        pants_frame.setVisibility(View.INVISIBLE);
        skirt_frame.setVisibility(View.INVISIBLE);
        dress_frame.setVisibility(View.INVISIBLE);

        final Spinner spinner_main=(Spinner)findViewById(R.id.spinner);
        final Spinner spinner_sub=(Spinner)findViewById(R.id.spinner_sub);
        adspin1=ArrayAdapter.createFromResource(this, R.array.카테고리, android.R.layout.simple_spinner_dropdown_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_main.setAdapter(adspin1);
        spinner_main.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adspin1.getItem(position).equals("상의")){
                    adspin2=ArrayAdapter.createFromResource(SelectClothesActivity.this, R.array.top, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_sub.setAdapter((adspin2));
                    spinner_sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //상의 레이아웃 나타나기
                            if(adspin2.getItem(position).equals("상의")){
                                top_frame.setVisibility(View.VISIBLE);
                                pants_frame.setVisibility(View.INVISIBLE);
                                skirt_frame.setVisibility(View.INVISIBLE);
                                dress_frame.setVisibility(View.INVISIBLE);
                            }

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else if(adspin1.getItem(position).equals("하의")){
                    adspin2=ArrayAdapter.createFromResource(SelectClothesActivity.this, R.array.bottom, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_sub.setAdapter((adspin2));
                    spinner_sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //바지 레이아웃 나타나기
                            if(adspin2.getItem(position).equals("바지")){
                                top_frame.setVisibility(View.INVISIBLE);
                                pants_frame.setVisibility(View.VISIBLE);
                                skirt_frame.setVisibility(View.INVISIBLE);
                                dress_frame.setVisibility(View.INVISIBLE);
                            }
                            //치마 레이아웃 나타나기
                            if(adspin2.getItem(position).equals("치마")){
                               //Toast.makeText(SelectClothesActivity.this, "치마 레이아웃 나타나기",Toast.LENGTH_SHORT).show();
                                top_frame.setVisibility(View.INVISIBLE);
                                pants_frame.setVisibility(View.INVISIBLE);
                                skirt_frame.setVisibility(View.VISIBLE);
                                dress_frame.setVisibility(View.INVISIBLE);
                            }

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else{//원피스
                    adspin2=ArrayAdapter.createFromResource(SelectClothesActivity.this, R.array.dress, android.R.layout.simple_spinner_dropdown_item);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_sub.setAdapter((adspin2));
                    spinner_sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //원피스 레이아웃 나타나기
                            if(adspin2.getItem(position).equals("원피스")){
                                top_frame.setVisibility(View.INVISIBLE);
                                pants_frame.setVisibility(View.INVISIBLE);
                                skirt_frame.setVisibility(View.INVISIBLE);
                                dress_frame.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    //레이아웃 변경 - 직접입력, qr코드
    public void changeView(int index){
        RelativeLayout input_frame=(RelativeLayout) findViewById(R.id.input_frame);
        RelativeLayout qr_frame=(RelativeLayout) findViewById(R.id.qr_frame);
        switch (index){
            case 1:
                input_frame.setVisibility(RelativeLayout.VISIBLE);
                qr_frame.setVisibility(RelativeLayout.INVISIBLE);
                break;

            case 2:
                input_frame.setVisibility(RelativeLayout.INVISIBLE);
                qr_frame.setVisibility(RelativeLayout.VISIBLE);
                break;
        }
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
