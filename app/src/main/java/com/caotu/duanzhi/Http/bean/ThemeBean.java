package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 所有的关注类似的页面都共用这个bean对象,包括发布里的话题搜索页面
 */

public class ThemeBean implements Parcelable {
    private String themeAvatar;
    private String themeName;
    private String themeSign;
    private boolean isFocus;
    private String userId;
    private boolean isMe;
    private boolean isTheme;
    private AuthBean auth;

    public AuthBean getAuth() {
        return auth;
    }

    public void setAuth(AuthBean auth) {
        this.auth = auth;
    }

    public ThemeBean() {
    }


    public String getThemeSign() {
        return themeSign;
    }

    public void setThemeSign(String themeSign) {
        this.themeSign = themeSign;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public boolean isTheme() {
        return isTheme;
    }

    public void setTheme(boolean theme) {
        isTheme = theme;
    }

    public String getThemeAvatar() {
        return themeAvatar;
    }

    public void setThemeAvatar(String themeAvatar) {
        this.themeAvatar = themeAvatar;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.themeAvatar);
        dest.writeString(this.themeName);
        dest.writeString(this.themeSign);
        dest.writeByte(this.isFocus ? (byte) 1 : (byte) 0);
        dest.writeString(this.userId);
        dest.writeByte(this.isMe ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTheme ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.auth, flags);
    }

    protected ThemeBean(Parcel in) {
        this.themeAvatar = in.readString();
        this.themeName = in.readString();
        this.themeSign = in.readString();
        this.isFocus = in.readByte() != 0;
        this.userId = in.readString();
        this.isMe = in.readByte() != 0;
        this.isTheme = in.readByte() != 0;
        this.auth = in.readParcelable(AuthBean.class.getClassLoader());
    }

    public static final Creator<ThemeBean> CREATOR = new Creator<ThemeBean>() {
        @Override
        public ThemeBean createFromParcel(Parcel source) {
            return new ThemeBean(source);
        }

        @Override
        public ThemeBean[] newArray(int size) {
            return new ThemeBean[size];
        }
    };
}
