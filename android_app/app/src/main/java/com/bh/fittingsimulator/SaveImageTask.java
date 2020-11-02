package com.bh.fittingsimulator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bh.fittingsimulator.glrender.Triangle;

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

import static android.content.Context.MODE_PRIVATE;

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
        String sUrl = "http://165.194.44.20:5000/fileUpload";
        String sUrl2 =  "http://165.194.44.20:5000/getModelData";

        Elements element;
        Connection.Response res;
        File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
        try {
//            Toast.makeText(calibrationActivity, "사진을 전송을 시작합니다.", Toast.LENGTH_SHORT).show();
            res  = Jsoup.connect(sUrl2).data("file",file.getName(), new FileInputStream(file)).method(Connection.Method.POST).execute();
            element = res.parse().select("h1");

//            Toast.makeText(calibrationActivity, "사진을 전송을 완료했습니다." + element.get(0).text() +"||"+ element.get(1).text(), Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences= calibrationActivity.getSharedPreferences("test", MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            String armString = element.get(0).text().split(":")[1];
            String bodyString = element.get(1).text().split(":")[1];
            String chestString = element.get(2).text().split(":")[1];
            String chestTobodyString = element.get(3).text().split(":")[1];
            String chestToShoulderString = element.get(4).text().split(":")[1];
            editor.putString("armString",armString);
            editor.putString("bodyString",bodyString);
            editor.putString("chestString",chestString);
            editor.putString("chestTobodyString",chestTobodyString);
            editor.putString("chestToShoulderString",chestToShoulderString);

            Triangle tempTriangle = new Triangle();
            tempTriangle.values = new float[]{
                    1630.0f,//height
                    Float.parseFloat(armString),//armhole
                    Float.parseFloat(chestString),//chest
                    Float.parseFloat(chestToShoulderString),//shoulder to chest
                    Float.parseFloat(bodyString),//waist
                    Float.parseFloat(chestTobodyString)//chest to waist
            };
            Log.d("abc", armString);


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

        Matrix m = new Matrix();
        m.setRotate(90, (float) source.getWidth() / 2, (float) source.getHeight() / 2);

        source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, true);


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


    public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) throws Exception {
        if(bitmap == null) return null;
        if (degrees == 0) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }




}