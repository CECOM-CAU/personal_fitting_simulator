package com.bh.fittingsimulator;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.bh.fittingsimulator.glrender.MyGLSurfaceView;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;
    FrameLayout testLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testLayout = findViewById(R.id.mainlayout);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,50,0,300);
        mGLView = new MyGLSurfaceView(this);
        mGLView.setLayoutParams(params);
        //mGLView.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        testLayout.addView(mGLView);


        //testcode
        Button btn = findViewById(R.id.testbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CalibrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
