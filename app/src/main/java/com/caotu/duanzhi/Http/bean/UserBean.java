package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ 搜索专用用户bean
 */
public class UserBean implements Parcelable {
    public String authpic;
    public String username;
    public String userheadphoto;
    public String userid;
    public String uno;
    public boolean isHeader;  //是否是头布局
    public boolean isFocus;  //是否是关注的头

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.authpic);
        dest.writeString(this.username);
        dest.writeString(this.userheadphoto);
        dest.writeString(this.userid);
        dest.writeString(this.uno);
        dest.writeByte(this.isHeader ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFocus ? (byte) 1 : (byte) 0);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.authpic = in.readString();
        this.username = in.readString();
        this.userheadphoto = in.readString();
        this.userid = in.readString();
        this.uno = in.readString();
        this.isHeader = in.readByte() != 0;
        this.isFocus = in.readByte() != 0;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
