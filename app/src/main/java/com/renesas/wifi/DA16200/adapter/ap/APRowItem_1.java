package com.renesas.wifi.DA16200.adapter.ap;

public class APRowItem_1 {
    private int imageId;
    private String ssid;
    private String stringSecurity;
    private int securityType;  //add in v2.3.13
    private int level;

    public APRowItem_1(int imageId, String ssid, String stringSecurity, int securityType, int level) {
        this.imageId = imageId;
        this.ssid = ssid;
        this.stringSecurity = stringSecurity;
        this.securityType = securityType;  //add in v2.3.13
        this.level = level;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getSSID() {
        return ssid;
    }
    public String getStringSecurity() {
        return stringSecurity;
    }

    public int getSecurityType() {
        return securityType;
    }  //add in v2.3.13
    public void setSSID(String ssid) {
        this.ssid = ssid;
    }
    @Override
    public String toString() {
        return ssid;
    }
    public int getLevel() {
        return level;
    }
}
