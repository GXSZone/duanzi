package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe 话题列表item对象
 */
public class TopicItemBean implements Parcelable {

    private String tagalias;
    private String tagid;
    private String tagimg;

    public String getTagalias() {
        return tagalias;
    }

    public void setTagalias(String tagalias) {
        this.tagalias = tagalias;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTagimg() {
        return tagimg;
    }

    public void setTagimg(String tagimg) {
        this.tagimg = tagimg;
    }

    @Override
    public String toString() {
        return "TagsBean{" +
                "tagalias='" + tagalias + '\'' +
                ", tagid='" + tagid + '\'' +
                ", tagimg='" + tagimg + '\'' +
                '}';

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tagalias);
        dest.writeString(this.tagid);
        dest.writeString(this.tagimg);
    }

    public TopicItemBean() {
    }

    protected TopicItemBean(Parcel in) {
        this.tagalias = in.readString();
        this.tagid = in.readString();
        this.tagimg = in.readString();
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
