package com.bh.fittingsimulator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SelectClothesActivity extends AppCompatActivity {

    private ArrayAdapter<CharSequence> adspin1,adspin2;
    private IntentIntegrator qrScan;
    private String[] clothes_data;
    private float[] arr=new float[5];

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

        //설명 페이지에 있는 큐알코드 이미지 투명도 조절
        Drawable qr_image=((ImageView)findViewById(R.id.ic_qr)).getDrawable();
        qr_image.setAlpha(80);
        qrScan= new IntentIntegrator(this);

        //레이아웃 변경 -qr_btn -qr코드 페이지
        qr_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input_btn.setSelected(!input_btn.isSelected());
                qr_btn.setSelected(!qr_btn.isSelected());
                changeView(2);
                //qrcode 스캔캔
                Button scan = findViewById(R.id.scan_btn);
                scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
                        qrScan.initiateScan();
                    }
                });
            }
        });



        //상의-확인 버튼 눌렀을때
        Button top_ok_btn=(Button) findViewById(R.id.top_ok_button);
        top_ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //메인화면으로

                //상의 치수 변수들 -> 서버로 보내기
                EditText top_shoulder=(EditText)findViewById(R.id.top_shoulder_et);//상의_어깨길이
                arr[0]=Float.parseFloat(top_shoulder.getText().toString());//더블형
                //Integer.parseInt(top_shoulder.getText().toString());//정수형

                EditText top_arm=(EditText)findViewById(R.id.top_arm_et);//상의_팔길이
                arr[1]=Float.parseFloat(top_arm.getText().toString());//더블형
                //Integer.parseInt(top_arm.getText().toString());//정수형

                EditText top_chest=(EditText)findViewById(R.id.top_chest_et);//상의_가슴단면
                arr[2]=Float.parseFloat(top_chest.getText().toString());//더블형
                //Integer.parseInt(top_chest.getText().toString());//정수형

                EditText top_arm_width=(EditText)findViewById(R.id.top_arm_width_et);//상의_소매폭
                arr[3]=Float.parseFloat(top_arm_width.getText().toString());//더블형
                //Integer.parseInt(top_arm_width.getText().toString());//정수형

                EditText top_total_len=(EditText)findViewById(R.id.top_total_len_et);//상의_총길이
                arr[4]=Float.parseFloat(top_total_len.getText().toString());//더블형
                //Integer.parseInt(top_total_len.getText().toString());//정수형

                Intent intent=new Intent(SelectClothesActivity.this,FittingActivity.class);
                intent.putExtra("arr",arr);
                startActivity(intent);
                finish();

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
            case R.id.setting:{
                Intent intent=new Intent(SelectClothesActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //수치대로 피팅하기
    protected void DataScan(String d){
        clothes_data= d.split(",");
        if(clothes_data!=null){
            String mes="어께길이: "+clothes_data[0]+"\n팔길이: "+clothes_data[1]+"\n가슴길이: "+clothes_data[2]+"\n암홀: "+clothes_data[3]+"\n총기장: "+clothes_data[4]+"\n피팅하겠습니까?";

            //알림창 띄어서 보여주기
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fittting Clothes");
            builder.setMessage(mes);
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();


                            //서버로 수치 보내기


                            Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(SelectClothesActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.show();
        }

        else
            return;
    }

    //qr코드 스캔되었을때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                //Toast.makeText(this, "Scanned:" + result.getContents(), Toast.LENGTH_LONG).show();
                //내용 나눠서 저장하기
                String num=result.getContents();
                DataScan(num);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
