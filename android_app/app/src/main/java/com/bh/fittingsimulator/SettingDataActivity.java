package com.bh.fittingsimulator;

public class SettingDataActivity {

    private int icon;
    private String titlename;
    private String explain;

    public SettingDataActivity(int icon, String title, String explain){
        this.icon = icon;
        this.titlename = title;
        this.explain = explain;
    }

    public int getIcon(){
        return this.icon;
    }

    public String getTitlename(){
        return this.titlename;
    }

    public String getExplain(){
        return this.explain;
    }

}
