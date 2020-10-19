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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveImageTask extends AsyncTask<Bitmap, Void, Void> {
    CalibrationActivity calibrationActivity;

    public SaveImageTask(CalibrationActivity calibrationActivity) throws IOException {
        this.calibrationActivity = calibrationActivity;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Toast.makeText(calibrationActivity, "사진을 저장하였습니다.", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected Void doInBackground(Bitmap... data) {
        Bitmap bitmap = null;
        try {
            bitmap = data[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("test","messagte");
        insertImage(calibrationActivity.getContentResolver(), bitmap, ""+System.currentTimeMillis(), "");
        Log.d("test","messagte2");

        Document doc;
        String sUrl = "http://10.0.2.2:5000/fileUpload";
        String sUrl2 =  "http://10.0.2.2:5000/";

        Elements element;
        Connection.Response res;
        File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
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


    //출처 - https://codeday.me/ko/qa/20190310/39556.html
    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not populated).
     * @see MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
     */


    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
                    fOut.close();
                    //res = Jsoup.connect(sUrl).data("file",file.getName(), new FileInputStream(file)).method(Connection.Method.POST).execute();
                    //element = res.parse().select("h1");
                } finally {
                    imageOut.close();
                }

            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }







}