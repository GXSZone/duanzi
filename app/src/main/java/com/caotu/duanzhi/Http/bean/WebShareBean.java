package com.caotu.duanzhi.Http.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.umeng.socialize.bean.SHARE_MEDIA;

public class WebShareBean implements Parcelable {
    public String icon;
    public String url;
    public String title;
    public String content;
    public String contentId;
    //分享的平台
    public SHARE_MEDIA medial;
    //是否是视频,默认不是视频
    public boolean isVideo;
    //是否已经收藏
    public boolean hasColloection;

    //type类型默认是链接形式
    public int webType;

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
        dest.writeString(this.contentId);
        dest.writeInt(this.medial == null ? -1 : this.medial.ordinal());
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasColloection ? (byte) 1 : (byte) 0);
        dest.writeInt(this.webType);
    }

    public WebShareBean() {
    }

    protected WebShareBean(Parcel in) {
        this.icon = in.readString();
        this.url = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.contentId = in.readString();
        int tmpMedial = in.readInt();
        this.medial = tmpMedial == -1 ? null : SHARE_MEDIA.values()[tmpMedial];
        this.isVideo = in.readByte() != 0;
        this.hasColloection = in.readByte() != 0;
        this.webType = in.readInt();
    }

    public static final Parcelable.Creator<WebShareBean> CREATOR = new Parcelable.Creator<WebShareBean>() {
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
