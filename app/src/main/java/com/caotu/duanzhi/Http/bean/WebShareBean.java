package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class WebShareBean implements Parcelable {
    public String icon;
    public String url;
    public String title;
    public String content;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.icon);
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeString(this.content);
    }

    public WebShareBean() {
    }

    protected WebShareBean(Parcel in) {
        this.icon = in.readString();
        this.url = in.readString();
        this.title = in.readString();
        this.content = in.readString();
    }

    public static final Creator<WebShareBean> CREATOR = new Creator<WebShareBean>() {
        @Override
        public WebShareBean createFromParcel(Parcel source) {
            return new WebShareBean(source);
        }

        @Override
        public WebShareBean[] newArray(int size) {
            return new WebShareBean[size];
        }
    };
}
