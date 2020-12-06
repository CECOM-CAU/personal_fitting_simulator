package com.bh.fittingsimulator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SaveTask extends AsyncTask<String, Void, Void> {
    ModelingVideoActivity modelingVideoActivity;

    public SaveTask(ModelingVideoActivity modelingVideoActivity) throws IOException {
        this.modelingVideoActivity = modelingVideoActivity;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Toast.makeText(modelingVideoActivity, "동영상을 저장하였습니다.", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected Void doInBackground(String... data) {
        String height = null;
        try {
            height = data[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("test","messagte");

        //insertImage(modelingVideoActivity.getContentResolver(), bitmap, ""+height, "");

        Document doc;
        String sUrl = "http://165.194.44.20/fileUpload";
        String sUrl2 =  "http://10.0.2.2:5000/";

        Elements element;
        Connection.Response res;
        File file = new File("/sdcard/FittingSimulator/modeling.mp4");
        try {
            res  = Jsoup.connect(sUrl).data("file",file.getName(), new FileInputStream(file)).method(Connection.Method.POST).execute();
            element = res.parse().select("h1");
            String returnString = element.get(0).text();
            Log.d("abc", returnString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}