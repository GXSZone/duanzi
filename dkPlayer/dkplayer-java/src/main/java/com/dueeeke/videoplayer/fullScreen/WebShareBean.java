package com.dueeeke.videoplayer.fullScreen;


import android.os.Parcel;
import android.os.Parcelable;


public class WebShareBean implements Parcelable {
    public String icon;
    public String url;
    public String title;
    public String content;
    public String contentId;
    //分享的平台
//    public SHARE_MEDIA medial;
    //是否是视频,默认不是视频
    public boolean isVideo;
    //是否已经收藏
    public boolean hasColloection;
    //收藏是否需要展示
    public boolean isNeedShowCollection;
    //type类型默认是链接形式  即值为0;
    public int webType;
    //视频的下载URL
    public String VideoUrl;
    //目前用于区别是来自评论的分享还是内容的分享.默认0,即是内容,1代表评论;
    public int contentOrComment;
    public String copyText;

    public WebShareBean() {
    }

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
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasColloection ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedShowCollection ? (byte) 1 : (byte) 0);
        dest.writeInt(this.webType);
        dest.writeString(this.VideoUrl);
        dest.writeInt(this.contentOrComment);
        dest.writeString(this.copyText);
    }

    protected WebShareBean(Parcel in) {
        this.icon = in.readString();
        this.url = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.contentId = in.readString();
        this.isVideo = in.readByte() != 0;
        this.hasColloection = in.readByte() != 0;
        this.isNeedShowCollection = in.readByte() != 0;
        this.webType = in.readInt();
        this.VideoUrl = in.readString();
        this.contentOrComment = in.readInt();
        this.copyText = in.readString();
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
