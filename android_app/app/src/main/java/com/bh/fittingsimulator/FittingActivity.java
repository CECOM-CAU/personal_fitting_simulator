package com.bh.fittingsimulator;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bh.fittingsimulator.glrender.MyFittingSurfaceView;
import com.bh.fittingsimulator.glrender.MyGLSurfaceView;

public class FittingActivity extends AppCompatActivity {

    private MyFittingSurfaceView mFittingView;
    private MyGLSurfaceView mGLView;
    private FrameLayout testLayout;
    private float[] arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitting);
        testLayout = findViewById(R.id.mainlayout);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        arr=new float[]{0.0f,0.0f,0.0f,0.0f,0.0f};

        Intent intent = getIntent();
        arr[0] = intent.getFloatExtra("shoulder",0.0f);
        arr[1] = intent.getFloatExtra("arm",0.0f);
        arr[2] = intent.getFloatExtra("chest",0.0f);
        arr[3] = intent.getFloatExtra("arm_width",0.0f);
        arr[4] = intent.getFloatExtra("total_len",0.0f);
        //Toast.makeText(FittingActivity.this, Double.toString(arr[0]),Toast.LENGTH_SHORT).show();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,200,0,300);


        /********/
        mFittingView = new MyFittingSurfaceView(this,arr);
        mFittingView.setLayoutParams(params);
        testLayout.addView(mFittingView);

        /*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,200,0,300);
        mGLView = new MyGLSurfaceView(this);
        mGLView.setLayoutParams(params);
        //mGLView.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        testLayout.addView(mGLView);*/

        //확인버튼
        Button btn = findViewById(R.id.testbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FittingActivity.this,MainActivity.class);
                intent.putExtra("shoulder",arr[0]);
                intent.putExtra("arm",arr[1]);
                intent.putExtra("chest",arr[2]);
                intent.putExtra("arm_width",arr[3]);
                intent.putExtra("total_len",arr[4]);
                startActivity(intent);

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

    @Override //툴바 메뉴
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{//뒤로가기
                finish();
                return true;
            }
            case R.id.setting:{
                Intent intent=new Intent(FittingActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}