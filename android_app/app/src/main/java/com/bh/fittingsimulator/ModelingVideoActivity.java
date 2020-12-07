package com.bh.fittingsimulator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ModelingVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Button btn_record;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;
    private  File dir;
    private String heightvalue=""; //키

    //private SaveVideoTask saveVideoTask;

    private int result=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modeling_video);

        //저장 폴더 만들기
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FittingSimulator");
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

        pose.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        AlertDialog.Builder ad = new AlertDialog.Builder(ModelingVideoActivity.this);
        ad.setTitle("");       // 제목 설정
        ad.setMessage("모델링을 위해 사용자의 키(신장)값을 입력해주세요");   // 내용 설정
        final EditText et = new EditText(ModelingVideoActivity.this);
        ad.setView(et);
        // 확인 버튼 설정
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Text 값 받아서 로그 남기기
                heightvalue = et.getText().toString();
                dialog.dismiss();
                pose.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
            }
        });
        // 취소 버튼 설정
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                Toast.makeText(ModelingVideoActivity.this, "키값이 입력되지 않아 녹화가 종료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModelingVideoActivity.this, ModelingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ad.show();



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


                    //시작 화면 메인 액티비티로 넘기기 위해 모델링 확인 파일 저장
                    File logFile = new File(dir +"/modeling.txt");

                    try {
                        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ModelingVideoActivity.this, LoadingActivity.class);
                    intent.putExtra("height",heightvalue);
                    startActivity(intent);
                    finish();


                   /* Log.v("result",Integer.toString(result));
                    //성공시
                    if(result==1){
                        Intent intent = new Intent(ModelingVideoActivity.this, ModelingSuccessActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{ //실패시
                        Intent intent = new Intent(ModelingVideoActivity.this, ModelingFailActivity.class);
                        startActivity(intent);
                        finish();
                    }*/


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(ModelingVideoActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
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
                /*Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //서버로 파일 보내기
                        saveVideoTask = new SaveVideoTask();
                        saveVideoTask.execute();
                    }
                }, 5000);*/
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
        /*Toast.makeText(ModelingVideoActivity.this, "녹화가 종료되었습니다.", Toast.LENGTH_SHORT).show();

        //다음 페이지로 넘어가기 -로딩 화면 추가??
        Intent intent = new Intent(ModelingVideoActivity.this, ModelingSuccessActivity.class);

        startActivity(intent);
        finish();*/
    }


    class SaveVideoTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
        }
        @Override
        protected Integer doInBackground(Void... data) {

            Log.d("test","messagte");

            Document doc;
            String sUrl = "http://165.194.44.20/fileUpload";
            String sUrl2 =  "http://10.0.2.2:5000/";

            Elements element;
            Connection.Response res;
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FittingSimulator/modeling.mp4");
            Log.v("file",file.getPath());

            try {
                res  = Jsoup.connect(sUrl).data("file",file.getName(), new FileInputStream(file)).data("height",heightvalue).method(Connection.Method.POST).execute();

                element = res.parse().select("h1");
                String returnString = element.get(0).text()+"#"+element.get(1).text();
                Log.d("abc", returnString);
                result=1;
            } catch (IOException e) {
                e.printStackTrace();
                result=0;
            }

            return result;
        }


    }
}


