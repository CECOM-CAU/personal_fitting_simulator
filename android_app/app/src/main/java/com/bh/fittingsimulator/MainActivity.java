package com.bh.fittingsimulator;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bh.fittingsimulator.glrender.MyGLSurfaceView;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;
    private FrameLayout testLayout;
    private double[] arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testLayout = findViewById(R.id.mainlayout);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,200,0,300);
        mGLView = new MyGLSurfaceView(this);
        mGLView.setLayoutParams(params);
        //mGLView.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        testLayout.addView(mGLView);

        //확인버튼
        ImageButton btn = findViewById(R.id.testbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SelectClothesActivity.class);
                startActivity(intent);
            }
        });

        Button clothesbtn = findViewById(R.id.clothesbutton);
        clothesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FittingActivity.class);
                intent.putExtra("shoulder",arr[0]);
                intent.putExtra("arm",arr[1]);
                intent.putExtra("chest",arr[2]);
                intent.putExtra("arm_width",arr[3]);
                intent.putExtra("total_len",arr[4]);
                startActivity(intent);
            }
        });

        arr=new double[]{0,0,0,0,0};
        Intent intent = getIntent();
        arr[0] = intent.getDoubleExtra("shoulder",0);
        arr[1] = intent.getDoubleExtra("arm",0);
        arr[2] = intent.getDoubleExtra("chest",0);
        arr[3] = intent.getDoubleExtra("arm_width",0);
        arr[4] = intent.getDoubleExtra("total_len",0);


    }

    //툴바 설정 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override //툴바 메뉴
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{//뒤로가기
                finish();
                return true;
            }
            case R.id.setting:{
                Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
