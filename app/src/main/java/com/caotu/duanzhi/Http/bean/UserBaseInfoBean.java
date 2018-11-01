package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/10 12:42
 */
public class UserBaseInfoBean implements Parcelable {


    /**
     * beFollowCount : 0
     * followCount : 0
     * goodCount : 25
     * userInfo : {"isfollow":"测试内容3prf","useraccount":"xuhuaxing","userbirthday":"19920825120212","userheadphoto":"http://www.baidu.com","userid":"f12f790jffhsidh","username":"徐华星","usersex":"1"}
     */

    private String beFollowCount;
    private String followCount;
    private String goodCount;
    private UserInfoBean userInfo;

    public String getBeFollowCount() {
        return beFollowCount;
    }

    public void setBeFollowCount(String beFollowCount) {
        this.beFollowCount = beFollowCount;
    }

    public String getFollowCount() {
        return followCount;
    }

    public void setFollowCount(String followCount) {
        this.followCount = followCount;
    }

    public String getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(String goodCount) {
        this.goodCount = goodCount;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfoBean implements Parcelable {
        /**
         * isfollow : 测试内容3prf
         * useraccount : xuhuaxing
         * userbirthday : 19920825120212
         * userheadphoto : http://www.baidu.com
         * userid : f12f790jffhsidh
         * username : 徐华星
         * usersex : 1
         */

        private String isfollow;
        private String usersign;
        private String useraccount;
        private String userbirthday;
        private String userheadphoto;
        private String userid;
        private String username;
        private String usersex;

        public String getIsfollow() {
            return isfollow;
        }

        public String getUsersign() {
            return usersign;
        }

        public void setUsersign(String usersign) {
            this.usersign = usersign;
        }

        public void setIsfollow(String isfollow) {
            this.isfollow = isfollow;
        }

        public String getUseraccount() {
            return useraccount;
        }

        public void setUseraccount(String useraccount) {
            this.useraccount = useraccount;
        }

        public String getUserbirthday() {
            return userbirthday;
        }

        public void setUserbirthday(String userbirthday) {
            this.userbirthday = userbirthday;
        }

        public String getUserheadphoto() {
            return userheadphoto;
        }

        public void setUserheadphoto(String userheadphoto) {
            this.userheadphoto = userheadphoto;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsersex() {
            return usersex;
        }

        public void setUsersex(String usersex) {
            this.usersex = usersex;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.isfollow);
            dest.writeString(this.useraccount);
            dest.writeString(this.userbirthday);
            dest.writeString(this.userheadphoto);
            dest.writeString(this.userid);
            dest.writeString(this.username);
            dest.writeString(this.usersex);
        }

        public UserInfoBean() {
        }

        protected UserInfoBean(Parcel in) {
            this.isfollow = in.readString();
            this.useraccount = in.readString();
            this.userbirthday = in.readString();
            this.userheadphoto = in.readString();
            this.userid = in.readString();
            this.username = in.readString();
            this.usersex = in.readString();
        }

        public static final Creator<UserInfoBean> CREATOR = new Creator<UserInfoBean>() {
            @Override
            public UserInfoBean createFromParcel(Parcel source) {
                return new UserInfoBean(source);
            }

            @Override
            public UserInfoBean[] newArray(int size) {
                return new UserInfoBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.beFollowCount);
        dest.writeString(this.followCount);
        dest.writeString(this.goodCount);
        dest.writeParcelable(this.userInfo, flags);
    }

    public UserBaseInfoBean() {
    }

    protected UserBaseInfoBean(Parcel in) {
        this.beFollowCount = in.readString();
        this.followCount = in.readString();
        this.goodCount = in.readString();
        this.userInfo = in.readParcelable(UserInfoBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserBaseInfoBean> CREATOR = new Parcelable.Creator<UserBaseInfoBean>() {
        @Override
        public UserBaseInfoBean createFromParcel(Parcel source) {
            return new UserBaseInfoBean(source);
        }

        @Override
        public UserBaseInfoBean[] newArray(int size) {
            return new UserBaseInfoBean[size];
        }
    };
}
