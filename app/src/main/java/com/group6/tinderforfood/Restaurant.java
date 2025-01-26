package com.group6.tinderforfood;

import java.util.List;

public class Restaurant {

    private String name;
    private String mainUrl;
    private String picUrl;
    private List<String> pictures;
    private String rating;
    private int currPic;
    private int iLast;

    public Restaurant(String name, String mainUrl) {
        this.name = name;
        setMainUrl(mainUrl);
        currPic = 0;
        iLast = 0;
    }

    public int getiLast() {
        return iLast;
    }

    public void setiLast(int iLast) {
        this.iLast = iLast;
    }

    public int getCurrPic() {
        return currPic;
    }

    public void incCurrPic() {
        this.currPic++;
    }

    public void decCurrPic() {
        this.currPic--;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
        this.picUrl = mainUrl.replace("/biz/", "/biz_photos/");
        this.picUrl += "?tab=food";
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}