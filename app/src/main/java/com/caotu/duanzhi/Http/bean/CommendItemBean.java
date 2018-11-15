package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class 评论列表的总Bean对象
 * @time 2018/7/19 14:04
 */
public class CommendItemBean implements Parcelable {

    /**
     * rows : [{"pageno":1,"pagesize":20,"start":0,"commentid":"c28485afdd1848b2b223d1bf782cf49d","userid":"f69c7f6d12dd4921abd55f683444fe29","contentid":"4783a8fedb944ebfaa155872cf99f301","commentreply":1,"commenttext":"噢噢噢哦哦噢噢噢哦哦","replycomment":"","commentgood":0,"username":"图友86023475","userheadphoto":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg","replyfirst":"","createtime":"20180719140342","replyCount":0,"isgood":"0"},{"pageno":1,"pagesize":20,"start":0,"commentid":"80a4a68e1e94489bb266f5c99c9b1dda","userid":"074a09a68a9f493eb999442b9cdf6a8d","contentid":"4783a8fedb944ebfaa155872cf99f301","commentreply":1,"commenttext":"这个太搞笑了吧🌝","replycomment":"","commentgood":0,"username":"派大星😄","userheadphoto":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/B1DB6A58-1975-4175-8DBC-0017583B4BE4.png","replyfirst":"","createtime":"20180718182627","replyCount":0,"isgood":"0"},{"pageno":1,"pagesize":20,"start":0,"commentid":"4e782941b2164357879997d36706072b","userid":"074a09a68a9f493eb999442b9cdf6a8d","contentid":"4783a8fedb944ebfaa155872cf99f301","commentreply":1,"commenttext":"😂","replycomment":"","commentgood":0,"username":"派大星😄","userheadphoto":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/B1DB6A58-1975-4175-8DBC-0017583B4BE4.png","replyfirst":"","createtime":"20180718181727","replyCount":0,"isgood":"0"}]
     * pageno : 1
     * pagesize : 20
     * count : 3
     */

    private int pageno;
    private int pagesize;
    private int count;
    private List<RowsBean> rows;
    private List<RowsBean> bestlist;
    //ugc 内容
    private RowsBean ugc;

    public RowsBean getUgc() {
        return ugc;
    }

    public void setUgc(RowsBean ugc) {
        this.ugc = ugc;
    }

    public List<RowsBean> getBestlist() {
        return bestlist;
    }

    public void setBestlist(List<RowsBean> bestlist) {
        this.bestlist = bestlist;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }


    public static class RowsBean implements Parcelable {
        /**
         * pageno : 1
         * pagesize : 20
         * start : 0
         * commentid : c28485afdd1848b2b223d1bf782cf49d
         * userid : f69c7f6d12dd4921abd55f683444fe29
         * contentid : 4783a8fedb944ebfaa155872cf99f301
         * commentreply : 1
         * commenttext : 噢噢噢哦哦噢噢噢哦哦
         * replycomment :
         * commentgood : 0
         * username : 图友86023475
         * userheadphoto : https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg
         * replyfirst :
         * createtime : 20180719140342
         * replyCount : 0
         * isgood : 0
         */
        public List<ChildListBean> childList;
        public boolean isBest;//是不是热门评论
        public boolean showHeadr;//显示头部
        public boolean isUgc; //是否是UGC内容,跳转方式不一样
        public int pageno;
        public int pagesize;
        public int start;
        public String commentid;
        public String userid;
        public String contentid;
        public int commentreply;
        public String commenttext;
        public String replycomment;
        public int commentgood;
        public String username;
        public String ruusername;
        public String ruuserid;
        public String userheadphoto;
        public String replyfirst;
        public String createtime;
        public int replyCount;
        public int isgood;
        //[{"cover": "资源封面URL", "type": 1横视频2竖视频3图片4GIF, "info": "资源URL"}]
        public String commenturl;
        //"0"_未赞未踩 "1"_已赞 "2"_已踩
        public String goodstatus;
        public String isfollow;

        public String getIsfollow() {
            return isfollow;
        }

        public void setIsfollow(String isfollow) {
            this.isfollow = isfollow;
        }

        public boolean isUgc() {
            return isUgc;
        }

        public void setUgc(boolean ugc) {
            isUgc = ugc;
        }

        public RowsBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.childList);
            dest.writeByte(this.isBest ? (byte) 1 : (byte) 0);
            dest.writeByte(this.showHeadr ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isUgc ? (byte) 1 : (byte) 0);
            dest.writeInt(this.pageno);
            dest.writeInt(this.pagesize);
            dest.writeInt(this.start);
            dest.writeString(this.commentid);
            dest.writeString(this.userid);
            dest.writeString(this.contentid);
            dest.writeInt(this.commentreply);
            dest.writeString(this.commenttext);
            dest.writeString(this.replycomment);
            dest.writeInt(this.commentgood);
            dest.writeString(this.username);
            dest.writeString(this.ruusername);
            dest.writeString(this.ruuserid);
            dest.writeString(this.userheadphoto);
            dest.writeString(this.replyfirst);
            dest.writeString(this.createtime);
            dest.writeInt(this.replyCount);
            dest.writeInt(this.isgood);
            dest.writeString(this.commenturl);
            dest.writeString(this.goodstatus);
            dest.writeString(this.isfollow);
        }

        protected RowsBean(Parcel in) {
            this.childList = in.createTypedArrayList(ChildListBean.CREATOR);
            this.isBest = in.readByte() != 0;
            this.showHeadr = in.readByte() != 0;
            this.isUgc = in.readByte() != 0;
            this.pageno = in.readInt();
            this.pagesize = in.readInt();
            this.start = in.readInt();
            this.commentid = in.readString();
            this.userid = in.readString();
            this.contentid = in.readString();
            this.commentreply = in.readInt();
            this.commenttext = in.readString();
            this.replycomment = in.readString();
            this.commentgood = in.readInt();
            this.username = in.readString();
            this.ruusername = in.readString();
            this.ruuserid = in.readString();
            this.userheadphoto = in.readString();
            this.replyfirst = in.readString();
            this.createtime = in.readString();
            this.replyCount = in.readInt();
            this.isgood = in.readInt();
            this.commenturl = in.readString();
            this.goodstatus = in.readString();
            this.isfollow = in.readString();
        }

        public static final Creator<RowsBean> CREATOR = new Creator<RowsBean>() {
            @Override
            public RowsBean createFromParcel(Parcel source) {
                return new RowsBean(source);
            }

            @Override
            public RowsBean[] newArray(int size) {
                return new RowsBean[size];
            }
        };
    }

    public static class ChildListBean implements Parcelable {
        public String commentid;
        public String commenttext;
        public String commenturl;
        public String userid;
        public String username;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.commentid);
            dest.writeString(this.commenttext);
            dest.writeString(this.commenturl);
            dest.writeString(this.userid);
            dest.writeString(this.username);
        }

        public ChildListBean() {
        }

        protected ChildListBean(Parcel in) {
            this.commentid = in.readString();
            this.commenttext = in.readString();
            this.commenturl = in.readString();
            this.userid = in.readString();
            this.username = in.readString();
        }

        public static final Creator<ChildListBean> CREATOR = new Creator<ChildListBean>() {
            @Override
            public ChildListBean createFromParcel(Parcel source) {
                return new ChildListBean(source);
            }

            @Override
            public ChildListBean[] newArray(int size) {
                return new ChildListBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageno);
        dest.writeInt(this.pagesize);
        dest.writeInt(this.count);
        dest.writeList(this.rows);
        dest.writeList(this.bestlist);
        dest.writeParcelable(this.ugc, flags);
    }

    public CommendItemBean() {
    }

    protected CommendItemBean(Parcel in) {
        this.pageno = in.readInt();
        this.pagesize = in.readInt();
        this.count = in.readInt();
        this.rows = new ArrayList<RowsBean>();
        in.readList(this.rows, RowsBean.class.getClassLoader());
        this.bestlist = new ArrayList<RowsBean>();
        in.readList(this.bestlist, RowsBean.class.getClassLoader());
        this.ugc = in.readParcelable(RowsBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommendItemBean> CREATOR = new Parcelable.Creator<CommendItemBean>() {
        @Override
        public CommendItemBean createFromParcel(Parcel source) {
            return new CommendItemBean(source);
        }

        @Override
        public CommendItemBean[] newArray(int size) {
            return new CommendItemBean[size];
        }
    };
}
