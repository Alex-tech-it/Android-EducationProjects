package com.example.androideducationdatastate;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanyItem implements Parcelable {

    private String companyName;
    private String companyFounder;

    public CompanyItem(String companyName, String companyFounder){
        this.companyName = companyName;
        this.companyFounder = companyFounder;
    }

    protected CompanyItem(Parcel in) {
        companyName = in.readString();
        companyFounder = in.readString();
    }

    public static final Creator<CompanyItem> CREATOR = new Creator<CompanyItem>() {
        @Override
        public CompanyItem createFromParcel(Parcel in) {
            return new CompanyItem(in);
        }

        @Override
        public CompanyItem[] newArray(int size) {
            return new CompanyItem[size];
        }
    };

    public String getCompanyName(){
        return this.companyName;
    }
    public void setCompanyName(String value){
        this.companyName = value;
    }

    public String getCompanyFounder(){
        return "Основатель: " + this.companyFounder;
    }
    public String getCompanyFounderField(){
        return this.companyFounder;
    }
    public void setCompanyFounder(String value){
        this.companyFounder = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyName);
        dest.writeString(companyFounder);
    }
}
