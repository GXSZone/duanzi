package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TopicItemBean implements Parcelable {
    String imageUrl;
    String title;
    String topicId;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.title);
        dest.writeString(this.topicId);
    }

    public TopicItemBean() {
    }

    protected TopicItemBean(Parcel in) {
        this.imageUrl = in.readString();
        this.title = in.readString();
        this.topicId = in.readString();
    }

    public static final Parcelable.Creator<TopicItemBean> CREATOR = new Parcelable.Creator<TopicItemBean>() {
        @Override
        public TopicItemBean createFromParcel(Parcel source) {
            return new TopicItemBean(source);
        }

        @Override
        public TopicItemBean[] newArray(int size) {
            return new TopicItemBean[size];
        }
    };
}
