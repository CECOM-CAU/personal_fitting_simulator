package com.bh.fittingsimulator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    ArrayList<SettingDataActivity> settinglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        this.InitializeMovieData();

        ListView listView = (ListView)findViewById(R.id.listView);
        final SettingAdapterActivity myAdapter = new SettingAdapterActivity(this,settinglist);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){

                /*Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getTitlename(),
                        Toast.LENGTH_LONG).show();*/

                switch (myAdapter.getItem(position).getTitlename()){
                    case "카메라 설정 다시하기":{
                        Intent intent=new Intent(SettingActivity.this,CalibrationExplainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "모델링 다시하기":{
                        Intent intent=new Intent(SettingActivity.this,ModelingActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "데이터 초기화":{
                        String dir="FittingSimulator";
                        //FittingSimulator 폴더 삭제
                        removeDir(dir);
                        Intent intent=new Intent(SettingActivity.this,CalibrationExplainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }
        });
    }


    public void InitializeMovieData()
    {
        settinglist = new ArrayList<SettingDataActivity>();

        settinglist.add(new SettingDataActivity(R.drawable.ic_chessboard, "카메라 설정 다시하기","체스판 사진을 다시 찍습니다"));
        settinglist.add(new SettingDataActivity(R.drawable.ic_human, "모델링 다시하기","인체 모델링을 다시할 수 있습니다"));
        settinglist.add(new SettingDataActivity(R.drawable.ic_reset, "데이터 초기화","앱 내의 데이터를 초기화합니다"));
    }

    @Override //툴바 메뉴
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{//뒤로가기
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //폴더 삭제
    public static void removeDir(String dirName) {
        String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dirName;

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FittingSimulator");
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList){
            if(childFile.isDirectory()) {
                removeDir(childFile.getAbsolutePath());    //하위 디렉토리
            }
            else{
                childFile.delete();    //하위 파일 삭제
            }
        }
        file.delete();    //root 삭제
    }
}

