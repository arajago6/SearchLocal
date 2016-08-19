package edu.iit.arajago6hawk.searchlocal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rasuishere on 2/12/16.
 */
public class BusinessData implements Parcelable{
    private String name = "";
    private String category = "";
    private String desc = "";
    private String address = "";
    private String city = "";
    private String phone = "";
    private String web = "";
    private String fb = "";
    private double lat = 0.0;
    private double lng = 0.0;
    private String imgName = "";
    private String pub = "";

    public BusinessData(){}

    public BusinessData(Parcel source){
        this.name = source.readString();
        this.category = source.readString();
        this.desc = source.readString();
        this.address = source.readString();
        this.city = source.readString();
        this.phone = source.readString();
        this.web = source.readString();
        this.fb = source.readString();
        this.imgName = source.readString();
        this.pub = source.readString();
        this.lat = source.readFloat();
        this.lng = source.readFloat();
    }

    public static final Creator<BusinessData> CREATOR = new Creator<BusinessData>() {
        public BusinessData createFromParcel(Parcel source) {
            return new BusinessData(source);
        }

        public BusinessData[] newArray(int size) {
            return new BusinessData[size];
        }
    };

    @Override
    public int describeContents() {
        // hashCode() of this class
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(desc);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(phone);
        dest.writeString(web);
        dest.writeString(fb);
        dest.writeString(imgName);
        dest.writeString(pub);
        dest.writeDouble(lat);
        dest.writeDouble(lng);

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() { return category; }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getWeb() {
        return web;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getFb() {
        return fb;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getPub() {
        return pub;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLng() { return lng; }

}