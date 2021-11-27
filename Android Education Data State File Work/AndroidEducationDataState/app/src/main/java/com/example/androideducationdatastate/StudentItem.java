package com.example.androideducationdatastate;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentItem implements Parcelable {

    private String fullName;        // First Name & Last Name
    private String gender;          // Male / Female
    private String programLang;     // Programming Languages
    private String preferredIDE;    // Preferred IDEs

    public StudentItem(String fullName, String gender,
                       String programLang, String preferredIDE){
        this.fullName = fullName;
        this.gender = gender;
        this.programLang = programLang;
        this.preferredIDE = preferredIDE;
    }

    protected StudentItem(Parcel in) {
        fullName = in.readString();
        gender = in.readString();
        programLang = in.readString();
        preferredIDE = in.readString();
    }

    public static final Creator<StudentItem> CREATOR = new Creator<StudentItem>() {
        @Override
        public StudentItem createFromParcel(Parcel in) {
            return new StudentItem(in);
        }

        @Override
        public StudentItem[] newArray(int size) {
            return new StudentItem[size];
        }
    };

    public String getFullName(){
        return this.fullName;
    }

    public void setFullName(String value){
        this.fullName = value;
    }

    public String getGender(){
        return "Пол: " + this.gender;
    }

    public String getGenderField(){
        return this.gender;
    }

    public void setGender(String value){
        this.gender = value;
    }

    public String getProgramLang(){
        return this.programLang;
    }

    public void setProgramLang(String value){
        this.programLang = value;
    }

    public String getPreferredIDE(){
        return this.preferredIDE;
    }

    public void setPreferredIDE(String value){
        this.preferredIDE = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(gender);
        dest.writeString(programLang);
        dest.writeString(preferredIDE);
    }
}
