package com.quan.hineighboorguide2.utils;

public class BackUpModel {
    public String appPackage,newAppPackage,inter,fb_inter,banner,nativeAd,rewarded;
    public Boolean isLive,isAdsShow,isShowGG;
    public int percent_banner,percent_inter,percent_native,numberNativeAd;

    public BackUpModel(String appPackage, String newAppPackage, String inter, String fb_inter, String banner, String nativeAd, String rewarded, Boolean isLive, Boolean isAdsShow, Boolean isShowGG, int percent_banner, int percent_inter, int percent_native, int numberNativeAd) {
        this.appPackage = appPackage;
        this.newAppPackage = newAppPackage;
        this.inter = inter;
        this.fb_inter = fb_inter;
        this.banner = banner;
        this.nativeAd = nativeAd;
        this.rewarded = rewarded;
        this.isLive = isLive;
        this.isAdsShow = isAdsShow;
        this.isShowGG = isShowGG;
        this.percent_banner = percent_banner;
        this.percent_inter = percent_inter;
        this.percent_native = percent_native;
        this.numberNativeAd = numberNativeAd;
    }

    public BackUpModel() {
    }
}