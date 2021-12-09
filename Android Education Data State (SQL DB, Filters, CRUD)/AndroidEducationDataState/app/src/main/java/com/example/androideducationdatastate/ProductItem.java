package com.example.androideducationdatastate;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductItem implements Parcelable {

    private String productName;
    private String productPrice;
    private String productType;

    public ProductItem(String productName, String productPrice, String productType){
        this.productName = productName;
        this.productPrice = productPrice;
        this.productType = productType;
    }

    protected ProductItem(Parcel in) {
        productName = in.readString();
        productPrice = in.readString();
    }
    public static final Creator<ProductItem> CREATOR = new Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };

    public String getProductName(){
        return this.productName;
    }
    public void setProductName(String value){
        this.productName = value;
    }

    public String getProductPrice(){
        return this.productPrice;
    }
    public void setProductPrice(String value){
        this.productPrice = value;
    }

    public String getProductType(){
        return this.productType;
    }
    public void setProductType(String value){
        this.productType = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productPrice);
    }
}
