package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/10 12:42
 */
public class UserBaseInfoBean implements Parcelable {

    /**
     * userInfo : {"usersex":"0","usersign":"工具一起了了了","userbirthday":"","auth":{"authid":"0001","authurl":"https://active.diqyj.cn/apph5page_nhdz/searchdz.html","authword":"官方认证","authpic":"[\"https://ctkj-1256675270.file.myqcloud.com/auth_gf_big_v1.png\",\"https://ctkj-1256675270.file.myqcloud.com/auth_gf_small_v1.png\"]"},"uno":"00515","userheadphoto":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/nhdz.png","userid":"0e7792e0f8ce44a2bb16986e23400e2c","useraccount":"","isfollow":"1","username":"段友83541607"}
     * followCount : 2
     * goodCount : 13
     * beFollowCount : 1
     */

    private UserInfoBean userInfo;
    private String followCount;
    private String goodCount;
    private String beFollowCount;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
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

    public String getBeFollowCount() {
        return beFollowCount;
    }

    public void setBeFollowCount(String beFollowCount) {
        this.beFollowCount = beFollowCount;
    }

    public static class UserInfoBean implements Parcelable {
        /**
         * usersex : 0
         * usersign : 工具一起了了了
         * userbirthday :
         * auth : {"authid":"0001","authurl":"https://active.diqyj.cn/apph5page_nhdz/searchdz.html","authword":"官方认证","authpic":"[\"https://ctkj-1256675270.file.myqcloud.com/auth_gf_big_v1.png\",\"https://ctkj-1256675270.file.myqcloud.com/auth_gf_small_v1.png\"]"}
         * uno : 00515
         * userheadphoto : https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/nhdz.png
         * userid : 0e7792e0f8ce44a2bb16986e23400e2c
         * useraccount :
         * isfollow : 1
         * username : 段友83541607
         */

        private String usersex;
        private String usersign;
        private String userbirthday;
        private AuthBean auth;
        private String uno;
        private String userheadphoto;
        private String userid;
        private String useraccount;
        private String isfollow;
        private String username;
        private List<HonorlistBean> honorlist;

        public List<HonorlistBean> getHonorlist() {
            return honorlist;
        }

        public void setHonorlist(List<HonorlistBean> honorlist) {
            this.honorlist = honorlist;
        }

        public static class HonorlistBean implements Parcelable {
            /**
             * detailinfo : 测试内容ex16
             * gethonortime : 1
             * honorid : 1
             * honorpic : 1
             * honorurl : 1
             * honorword : 1
             * level : 1
             * levelinfo : {"checknum":1,"dvalue":1,"pic1":1,"pic2":1,"pic3":1,"picup":1,"word":1}
             */

            public String detailinfo;
            public String gethonortime;
            public String honorid;
            public String honorpic;
            public String honorurl;
            public String honorword;
            public String level;
            public LevelinfoBean levelinfo;

            public static class LevelinfoBean implements Parcelable {
                /**
                 * checknum : 1
                 * dvalue : 1
                 * pic1 : 1
                 * pic2 : 1
                 * pic3 : 1
                 * picup : 1
                 * word : 1
                 */

                public String checknum;
                public String dvalue;
                //勋章对应图片,大中小
                public String pic1;
                public String pic2;
                public String pic3;
                public String picup;
                public String word;

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.checknum);
                    dest.writeString(this.dvalue);
                    dest.writeString(this.pic1);
                    dest.writeString(this.pic2);
                    dest.writeString(this.pic3);
                    dest.writeString(this.picup);
                    dest.writeString(this.word);
                }

                public LevelinfoBean() {
                }

                protected LevelinfoBean(Parcel in) {
                    this.checknum = in.readString();
                    this.dvalue = in.readString();
                    this.pic1 = in.readString();
                    this.pic2 = in.readString();
                    this.pic3 = in.readString();
                    this.picup = in.readString();
                    this.word = in.readString();
                }

                public static final Creator<LevelinfoBean> CREATOR = new Creator<LevelinfoBean>() {
                    @Override
                    public LevelinfoBean createFromParcel(Parcel source) {
                        return new LevelinfoBean(source);
                    }

                    @Override
                    public LevelinfoBean[] newArray(int size) {
                        return new LevelinfoBean[size];
                    }
                };
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.detailinfo);
                dest.writeString(this.gethonortime);
                dest.writeString(this.honorid);
                dest.writeString(this.honorpic);
                dest.writeString(this.honorurl);
                dest.writeString(this.honorword);
                dest.writeString(this.level);
                dest.writeParcelable(this.levelinfo, flags);
            }

            public HonorlistBean() {
            }

            protected HonorlistBean(Parcel in) {
                this.detailinfo = in.readString();
                this.gethonortime = in.readString();
                this.honorid = in.readString();
                this.honorpic = in.readString();
                this.honorurl = in.readString();
                this.honorword = in.readString();
                this.level = in.readString();
                this.levelinfo = in.readParcelable(LevelinfoBean.class.getClassLoader());
            }

            public static final Creator<HonorlistBean> CREATOR = new Creator<HonorlistBean>() {
                @Override
                public HonorlistBean createFromParcel(Parcel source) {
                    return new HonorlistBean(source);
                }

                @Override
                public HonorlistBean[] newArray(int size) {
                    return new HonorlistBean[size];
                }
            };
        }

        public String getUsersex() {
            return usersex;
        }

        public void setUsersex(String usersex) {
            this.usersex = usersex;
        }

        public String getUsersign() {
            return usersign;
        }

        public void setUsersign(String usersign) {
            this.usersign = usersign;
        }

        public String getUserbirthday() {
            return userbirthday;
        }

        public void setUserbirthday(String userbirthday) {
            this.userbirthday = userbirthday;
        }

        public AuthBean getAuth() {
            return auth;
        }

        public void setAuth(AuthBean auth) {
            this.auth = auth;
        }

        public String getUno() {
            return uno;
        }

        public void setUno(String uno) {
            this.uno = uno;
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

        public String getUseraccount() {
            return useraccount;
        }

        public void setUseraccount(String useraccount) {
            this.useraccount = useraccount;
        }

        public String getIsfollow() {
            return isfollow;
        }

        public void setIsfollow(String isfollow) {
            this.isfollow = isfollow;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }


        public UserInfoBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.usersex);
            dest.writeString(this.usersign);
            dest.writeString(this.userbirthday);
            dest.writeParcelable(this.auth, flags);
            dest.writeString(this.uno);
            dest.writeString(this.userheadphoto);
            dest.writeString(this.userid);
            dest.writeString(this.useraccount);
            dest.writeString(this.isfollow);
            dest.writeString(this.username);
            dest.writeTypedList(this.honorlist);
        }

        protected UserInfoBean(Parcel in) {
            this.usersex = in.readString();
            this.usersign = in.readString();
            this.userbirthday = in.readString();
            this.auth = in.readParcelable(AuthBean.class.getClassLoader());
            this.uno = in.readString();
            this.userheadphoto = in.readString();
            this.userid = in.readString();
            this.useraccount = in.readString();
            this.isfollow = in.readString();
            this.username = in.readString();
            this.honorlist = in.createTypedArrayList(HonorlistBean.CREATOR);
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
        dest.writeParcelable(this.userInfo, flags);
        dest.writeString(this.followCount);
        dest.writeString(this.goodCount);
        dest.writeString(this.beFollowCount);
    }

    public UserBaseInfoBean() {
    }

    protected UserBaseInfoBean(Parcel in) {
        this.userInfo = in.readParcelable(UserInfoBean.class.getClassLoader());
        this.followCount = in.readString();
        this.goodCount = in.readString();
        this.beFollowCount = in.readString();
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
