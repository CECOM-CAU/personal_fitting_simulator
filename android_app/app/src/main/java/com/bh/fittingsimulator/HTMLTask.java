package com.bh.fittingsimulator;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HTMLTask extends AsyncTask<String, Void, String> {
    String sUrl = "http://127.0.0.1:5000/test";
    String routeUrl;
    String loginCode;
    String id;
    String pw;
    String time = "temp";
    private Elements element;


    @Override
    protected void onPostExecute(String html) {
        super.onPostExecute(html);
        //startActivity(activity,result,result.getExtras());
    }


    @Override
    protected String doInBackground(String... parm) {
        String returnString = "";
        Connection.Response res;
        Document doc;


        Log.d("test",returnString);
        return returnString;
    }
}