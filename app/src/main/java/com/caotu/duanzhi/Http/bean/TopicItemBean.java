package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe 话题列表item对象
 */
public class TopicItemBean implements Parcelable {

  public String tagalias;
  public String tagid;
  public String tagimg;
  public String activecount;

    public TopicItemBean() {
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
        dest.writeString(this.activecount);
    }

    protected TopicItemBean(Parcel in) {
        this.tagalias = in.readString();
        this.tagid = in.readString();
        this.tagimg = in.readString();
        this.activecount = in.readString();
    }

    public static final Creator<TopicItemBean> CREATOR = new Creator<TopicItemBean>() {
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
