package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.sunfusheng.widget.ImageData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 所有内容列表的展示对象
 */

public class MomentsDataBean implements Parcelable {

    /**
     * bestmap : [{"commentgood":1,"commentid":1,"commenttext":1,"userheadphoto":1,"userid":1,"username":1}]
     * contentbad : 17553
     * contentcomment : 0
     * contentgood : 1
     * contentid : 73d6592a600b11e8afee309c23a27303
     * contentlevel : 3
     * contenttag : 8c5f;5283;8yhn;
     * contenttext :
     * contenttitle : #宠物##猫星人##心都被萌化了#高冷p总的反差萌
     * contenttype : 1
     * contentuid : ae007cd921884577a0726bc495ff102c
     * contenturllist : ["https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/e51960f78dfd49b79aacd439d012fefd.jpg","https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/c4d9e2e972f649a9b139b19bfa5f486f.mp4"]
     * createtime : 20180531074647
     * isfollow : N
     * isshowtitle : 1
     * playcount : 47477
     * pushcount : 0
     * readcount : 0
     * repeatcount : 0
     * showtime : 1
     * tagshow : 1
     * tagshowid : 测试内容ih18
     * userheadphoto : 1
     * username : 1
     */
    //自定义字段
    public ArrayList<ImageData> imgList;  //图片集合
    public boolean isMySelf;        //判断是否是自己
    public boolean isShowCheckAll;
    public boolean isExpanded;
    public String authPic;
    public String fromCommentId;

    private int contentbad;
    private int contentcomment;
    private int contentgood;
    private String contentid;
    private String contentlevel;
    private String contenttag;
    private String contenttext;
    private String contenttitle;
    private String contenttype;
    private String contentuid;
    private String createtime;
    private String isfollow;
    private String isshowtitle;
    //这些数值类型都用string接收,以防解析出错
    private String playcount;
    private String pushcount;
    private String readcount;
    private String repeatcount;
    private String showtime;
    private String tagshow;
    private String tagshowid;
    private String userheadphoto;
    private String username;

    private BestmapBean bestmap;
    private AuthBean bestauth;
    private AuthBean auth;
    private String guajianurl;
    //神评的挂件
    private String bestguajian;

    public String getBestguajian() {
        return bestguajian;
    }

    public void setBestguajian(String bestguajian) {
        this.bestguajian = bestguajian;
    }

    public String getGuajianurl() {
        return guajianurl;
    }

    public void setGuajianurl(String guajianurl) {
        this.guajianurl = guajianurl;
    }

    public AuthBean getBestauth() {
        return bestauth;
    }

    public void setBestauth(AuthBean bestauth) {
        this.bestauth = bestauth;
    }

    private String contenturllist;
    //"0"_未赞未踩 "1"_已赞 "2"_已踩
    private String goodstatus;
    //只有登录状态下返回该字段,如果没有登录唤起登录页
    private String iscollection;
    //判断当前内容是否已经被删除  0_正常 1_已删除 2_审核中
    private String contentstatus;


    public AuthBean getAuth() {
        return auth;
    }

    public void setAuth(AuthBean auth) {
        this.auth = auth;
    }

    public String getContentstatus() {
        return contentstatus;
    }

    public void setContentstatus(String contentstatus) {
        this.contentstatus = contentstatus;
    }

    public String getGoodstatus() {
        return goodstatus;
    }

    public void setGoodstatus(String goodstatus) {
        this.goodstatus = goodstatus;
    }

    public String getIscollection() {
        return iscollection;
    }

    public void setIscollection(String iscollection) {
        this.iscollection = iscollection;
    }

    public BestmapBean getBestmap() {
        return bestmap;
    }

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public String getPushcount() {
        return pushcount;
    }

    public void setPushcount(String pushcount) {
        this.pushcount = pushcount;
    }

    public String getReadcount() {
        return readcount;
    }

    public void setReadcount(String readcount) {
        this.readcount = readcount;
    }

    public String getRepeatcount() {
        return repeatcount;
    }

    public void setRepeatcount(String repeatcount) {
        this.repeatcount = repeatcount;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }

    public void setBestmap(BestmapBean bestmap) {
        this.bestmap = bestmap;
    }


    public int getContentbad() {
        return contentbad;
    }

    public void setContentbad(int contentbad) {
        this.contentbad = contentbad;
    }

    public int getContentcomment() {
        return contentcomment;
    }

    public void setContentcomment(int contentcomment) {
        this.contentcomment = contentcomment;
    }

    public int getContentgood() {
        return contentgood;
    }

    public void setContentgood(int contentgood) {
        this.contentgood = contentgood;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getContentlevel() {
        return contentlevel;
    }

    public void setContentlevel(String contentlevel) {
        this.contentlevel = contentlevel;
    }

    public String getContenttag() {
        return contenttag;
    }

    public void setContenttag(String contenttag) {
        this.contenttag = contenttag;
    }

    public String getContenttext() {
        return contenttext;
    }

    public void setContenttext(String contenttext) {
        this.contenttext = contenttext;
    }

    public String getContenttitle() {
        return contenttitle;
    }

    public void setContenttitle(String contenttitle) {
        this.contenttitle = contenttitle;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getContentuid() {
        return contentuid;
    }

    public void setContentuid(String contentuid) {
        this.contentuid = contentuid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getIsfollow() {
        return isfollow;
    }

    public void setIsfollow(String isfollow) {
        this.isfollow = isfollow;
    }

    public String getIsshowtitle() {
        return isshowtitle;
    }

    public void setIsshowtitle(String isshowtitle) {
        this.isshowtitle = isshowtitle;
    }


    public String getTagshow() {
        return tagshow;
    }

    public void setTagshow(String tagshow) {
        this.tagshow = tagshow;
    }

    public String getTagshowid() {
        return tagshowid;
    }

    public void setTagshowid(String tagshowid) {
        this.tagshowid = tagshowid;
    }

    public String getUserheadphoto() {
        return userheadphoto;
    }

    public void setUserheadphoto(String userheadphoto) {
        this.userheadphoto = userheadphoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getContenturllist() {
        return contenturllist;
    }

    public void setContenturllist(String contenturllist) {
        this.contenturllist = contenturllist;
    }

    public static class BestmapBean implements Parcelable, Serializable {
        /**
         * commentgood : 1
         * commentid : 1
         * commenttext : 1
         * userheadphoto : 1
         * userid : 1
         * username : 1
         */


        private String commentgood;
        private String commentid;
        private String commenttext;
        private String userheadphoto;
        private String userid;
        private String username;
        //评论里的图片和视频   [{"cover": "资源封面URL", "type": 1横视频2竖视频3图片4GIF, "info": "资源URL"}]
        private String commenturl;
        //记录神评 "0"_未赞未踩 "1"_已赞 "2"_已踩
        private String goodstatus;


        public String getGoodstatus() {
            return goodstatus;
        }

        public void setGoodstatus(String goodstatus) {
            this.goodstatus = goodstatus;
        }

        public String getCommenturl() {
            return commenturl;
        }

        public void setCommenturl(String commenturl) {
            this.commenturl = commenturl;
        }

        public String getCommentgood() {
            return commentgood;
        }

        public void setCommentgood(String commentgood) {
            this.commentgood = commentgood;
        }

        public String getCommentid() {
            return commentid;
        }

        public void setCommentid(String commentid) {
            this.commentid = commentid;
        }

        public String getCommenttext() {
            return commenttext;
        }

        public void setCommenttext(String commenttext) {
            this.commenttext = commenttext;
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

        public BestmapBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.commentgood);
            dest.writeString(this.commentid);
            dest.writeString(this.commenttext);
            dest.writeString(this.userheadphoto);
            dest.writeString(this.userid);
            dest.writeString(this.username);
            dest.writeString(this.commenturl);
            dest.writeString(this.goodstatus);
        }

        protected BestmapBean(Parcel in) {
            this.commentgood = in.readString();
            this.commentid = in.readString();
            this.commenttext = in.readString();
            this.userheadphoto = in.readString();
            this.userid = in.readString();
            this.username = in.readString();
            this.commenturl = in.readString();
            this.goodstatus = in.readString();
        }

        public static final Creator<BestmapBean> CREATOR = new Creator<BestmapBean>() {
            @Override
            public BestmapBean createFromParcel(Parcel source) {
                return new BestmapBean(source);
            }

            @Override
            public BestmapBean[] newArray(int size) {
                return new BestmapBean[size];
            }
        };
    }

    public MomentsDataBean() {
    }

    @Override
    public String toString() {
        return "MomentsDataBean{" +
                "contentid='" + contentid + '\'' +
                ", contenttitle='" + contenttitle + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.imgList);
        dest.writeByte(this.isMySelf ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowCheckAll ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isExpanded ? (byte) 1 : (byte) 0);
        dest.writeString(this.authPic);
        dest.writeString(this.fromCommentId);
        dest.writeInt(this.contentbad);
        dest.writeInt(this.contentcomment);
        dest.writeInt(this.contentgood);
        dest.writeString(this.contentid);
        dest.writeString(this.contentlevel);
        dest.writeString(this.contenttag);
        dest.writeString(this.contenttext);
        dest.writeString(this.contenttitle);
        dest.writeString(this.contenttype);
        dest.writeString(this.contentuid);
        dest.writeString(this.createtime);
        dest.writeString(this.isfollow);
        dest.writeString(this.isshowtitle);
        dest.writeString(this.playcount);
        dest.writeString(this.pushcount);
        dest.writeString(this.readcount);
        dest.writeString(this.repeatcount);
        dest.writeString(this.showtime);
        dest.writeString(this.tagshow);
        dest.writeString(this.tagshowid);
        dest.writeString(this.userheadphoto);
        dest.writeString(this.username);
        dest.writeParcelable(this.bestmap, flags);
        dest.writeParcelable(this.bestauth, flags);
        dest.writeParcelable(this.auth, flags);
        dest.writeString(this.guajianurl);
        dest.writeString(this.bestguajian);
        dest.writeString(this.contenturllist);
        dest.writeString(this.goodstatus);
        dest.writeString(this.iscollection);
        dest.writeString(this.contentstatus);
    }

    protected MomentsDataBean(Parcel in) {
        this.imgList = in.createTypedArrayList(ImageData.CREATOR);
        this.isMySelf = in.readByte() != 0;
        this.isShowCheckAll = in.readByte() != 0;
        this.isExpanded = in.readByte() != 0;
        this.authPic = in.readString();
        this.fromCommentId = in.readString();
        this.contentbad = in.readInt();
        this.contentcomment = in.readInt();
        this.contentgood = in.readInt();
        this.contentid = in.readString();
        this.contentlevel = in.readString();
        this.contenttag = in.readString();
        this.contenttext = in.readString();
        this.contenttitle = in.readString();
        this.contenttype = in.readString();
        this.contentuid = in.readString();
        this.createtime = in.readString();
        this.isfollow = in.readString();
        this.isshowtitle = in.readString();
        this.playcount = in.readString();
        this.pushcount = in.readString();
        this.readcount = in.readString();
        this.repeatcount = in.readString();
        this.showtime = in.readString();
        this.tagshow = in.readString();
        this.tagshowid = in.readString();
        this.userheadphoto = in.readString();
        this.username = in.readString();
        this.bestmap = in.readParcelable(BestmapBean.class.getClassLoader());
        this.bestauth = in.readParcelable(AuthBean.class.getClassLoader());
        this.auth = in.readParcelable(AuthBean.class.getClassLoader());
        this.guajianurl = in.readString();
        this.bestguajian = in.readString();
        this.contenturllist = in.readString();
        this.goodstatus = in.readString();
        this.iscollection = in.readString();
        this.contentstatus = in.readString();
    }

    public static final Creator<MomentsDataBean> CREATOR = new Creator<MomentsDataBean>() {
        @Override
        public MomentsDataBean createFromParcel(Parcel source) {
            return new MomentsDataBean(source);
        }

        @Override
        public MomentsDataBean[] newArray(int size) {
            return new MomentsDataBean[size];
        }
    };
}
