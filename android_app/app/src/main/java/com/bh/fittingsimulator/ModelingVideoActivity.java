package com.bh.fittingsimulator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ModelingVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Button btn_record;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modeling_video);

        //저장 폴더 만들기
        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FittingSimulator");
        if(!dir.exists()){
            dir.mkdirs();
        }
        final ImageView pose=(ImageView)findViewById(R.id.video_image);
        final TextView text=(TextView)findViewById(R.id.video_text);

        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("녹화를 위하여 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다. 설정 > 권한에서 허용해주세요.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();

        btn_record = findViewById(R.id.surface_button);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording = false;

                    Toast.makeText(ModelingVideoActivity.this, "녹화가 종료되었습니다.", Toast.LENGTH_SHORT).show();

                    //다음 페이지로 넘어가기 -로딩 화면 추가??
                    Intent intent = new Intent(ModelingVideoActivity.this, ModelingSuccessActivity.class);

                    //시작 화면 메인 액티비티로 넘기기 위해 모델링 확인 파일 저장
                    File logFile = new File(dir +"/modeling.txt");

                    try {
                        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent);
                    finish();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ModelingVideoActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                            pose.setVisibility(View.GONE);
                            text.setVisibility(View.GONE);
                            try {
                                mediaRecorder = new MediaRecorder();
                                camera.unlock();
                                mediaRecorder.setCamera(camera);
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                                mediaRecorder.setOrientationHint(90);
                                mediaRecorder.setOutputFile("sdcard/FittingSimulator/modeling.mp4");
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                recording = true;

                            } catch (Exception e) {
                                e.printStackTrace();
                                mediaRecorder.release();

                            }
                        }
                    });
                }
            }
        });

    }

    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //Toast.makeText(ModelingVideoActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            camera = Camera.open();
            camera.setDisplayOrientation(90);
            surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(ModelingVideoActivity.this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(ModelingVideoActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    private void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {

        camera = cam;

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

