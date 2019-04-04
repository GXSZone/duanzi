package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 用户标签图标类
 */
public class AuthBean implements Parcelable, Serializable {
    /**
     * authid : 0001
     * authurl : https://active.diqyj.cn/apph5page_nhdz/searchdz.html
     * authword : 官方认证
     * authpic : ["https://ctkj-1256675270.file.myqcloud.com/auth_gf_big_v1.png","https://ctkj-1256675270.file.myqcloud.com/auth_gf_small_v1.png"]
     */

    private String authid;
    private String authurl;
    private String authword;
    private String authpic;

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public String getAuthurl() {
        return authurl;
    }

    public void setAuthurl(String authurl) {
        this.authurl = authurl;
    }

    public String getAuthword() {
        return authword;
    }

    public void setAuthword(String authword) {
        this.authword = authword;
    }

    public String getAuthpic() {
        return authpic;
    }

    public void setAuthpic(String authpic) {
        this.authpic = authpic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.authid);
        dest.writeString(this.authurl);
        dest.writeString(this.authword);
        dest.writeString(this.authpic);
    }

    public AuthBean() {
    }

    protected AuthBean(Parcel in) {
        this.authid = in.readString();
        this.authurl = in.readString();
        this.authword = in.readString();
        this.authpic = in.readString();
    }

    public static final Parcelable.Creator<AuthBean> CREATOR = new Parcelable.Creator<AuthBean>() {
        @Override
        public AuthBean createFromParcel(Parcel source) {
            return new AuthBean(source);
        }

        @Override
        public AuthBean[] newArray(int size) {
            return new AuthBean[size];
        }
    };
}
