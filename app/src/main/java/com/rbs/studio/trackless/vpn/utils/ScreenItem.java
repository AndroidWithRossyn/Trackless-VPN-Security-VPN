package com.rbs.studio.trackless.vpn.utils;

public class ScreenItem {
    String Title, Description, Bt, Skip;
    int ScreenImg;

    public ScreenItem(String title, String description, int screenImg, String bt, String skip) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
        Bt = bt;
        Skip = skip;
    }

    public String getBt() {
        return Bt;
    }

    public void setBt(String bt) {
        Bt = bt;
    }

    public String getSkip() {
        return Skip;
    }

    public void setSkip(String skip) {
        Skip = skip;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }
}
