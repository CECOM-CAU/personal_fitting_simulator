package com.bh.fittingsimulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingAdapterActivity extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<SettingDataActivity> sample;

    public SettingAdapterActivity(Context context, ArrayList<SettingDataActivity> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SettingDataActivity getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.activity_setting_list, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.icon);
        TextView movieName = (TextView)view.findViewById(R.id.title);
        TextView grade = (TextView)view.findViewById(R.id.explain);

        imageView.setImageResource(sample.get(position).getIcon());
        movieName.setText(sample.get(position).getTitlename());
        grade.setText(sample.get(position).getExplain());

        return view;
    }
}
