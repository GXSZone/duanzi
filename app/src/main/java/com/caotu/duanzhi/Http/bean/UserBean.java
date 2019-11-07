package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 列表展示使用,包括搜索用户以及关注用户,粉丝列表点赞的人列表等,全部统一使用该对象
 */
public class UserBean implements Parcelable {
    public String authpic;
    public String username;
    public String userheadphoto;
    public String userid;
    public String uno;
    public boolean isFocus;  //是否关注
    //为了分组头布局添加的字段
    public String groupId;
    //是否是自己
    public boolean isMe;
    public String authname;
    public String guajianurl;

    public UserBean() {
    }

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
        dest.writeByte(this.isFocus ? (byte) 1 : (byte) 0);
        dest.writeString(this.groupId);
        dest.writeByte(this.isMe ? (byte) 1 : (byte) 0);
        dest.writeString(this.authname);
        dest.writeString(this.guajianurl);
    }

    protected UserBean(Parcel in) {
        this.authpic = in.readString();
        this.username = in.readString();
        this.userheadphoto = in.readString();
        this.userid = in.readString();
        this.uno = in.readString();
        this.isFocus = in.readByte() != 0;
        this.groupId = in.readString();
        this.isMe = in.readByte() != 0;
        this.authname = in.readString();
        this.guajianurl = in.readString();
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
