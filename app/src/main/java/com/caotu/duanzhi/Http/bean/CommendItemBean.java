package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class 评论列表的总Bean对象
 * @time 2018/7/19 14:04
 */
public class CommendItemBean {

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

        private boolean isBest;//是不是热门评论
        private boolean showHeadr;//显示头部
        private boolean hideTail;//隐藏尾部头部
        private int pageno;
        private int pagesize;
        private int start;
        private String commentid;
        private String userid;
        private String contentid;
        private int commentreply;
        private String commenttext;
        private String replycomment;
        private int commentgood;
        private String username;
        private String ruusername;
        private String ruuserid;
        private String userheadphoto;
        private String guajianurl;
        private String replyfirst;
        private String createtime;
        private int replyCount;
        private int isgood;

        public String getRuuserid() {
            return ruuserid;
        }

        public void setRuuserid(String ruuserid) {
            this.ruuserid = ruuserid;
        }

        public String getGuajianurl() {
            return guajianurl;
        }

        public void setGuajianurl(String guajianurl) {
            this.guajianurl = guajianurl;
        }

        public boolean isHideTail() {
            return hideTail;
        }

        public void setHideTail(boolean hideTail) {
            this.hideTail = hideTail;
        }

        public boolean isBest() {
            return isBest;
        }

        public void setBest(boolean best) {
            isBest = best;
        }

        public boolean isShowHeadr() {
            return showHeadr;
        }

        public void setShowHeadr(boolean showHeadr) {
            this.showHeadr = showHeadr;
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

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public String getCommentid() {
            return commentid;
        }

        public void setCommentid(String commentid) {
            this.commentid = commentid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getContentid() {
            return contentid;
        }

        public void setContentid(String contentid) {
            this.contentid = contentid;
        }

        public int getCommentreply() {
            return commentreply;
        }

        public void setCommentreply(int commentreply) {
            this.commentreply = commentreply;
        }

        public String getCommenttext() {
            return commenttext;
        }

        public void setCommenttext(String commenttext) {
            this.commenttext = commenttext;
        }

        public String getReplycomment() {
            return replycomment;
        }

        public void setReplycomment(String replycomment) {
            this.replycomment = replycomment;
        }

        public int getCommentgood() {
            return commentgood;
        }

        public void setCommentgood(int commentgood) {
            this.commentgood = commentgood;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserheadphoto() {
            return userheadphoto;
        }

        public void setUserheadphoto(String userheadphoto) {
            this.userheadphoto = userheadphoto;
        }

        public String getReplyfirst() {
            return replyfirst;
        }

        public void setReplyfirst(String replyfirst) {
            this.replyfirst = replyfirst;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public int getIsgood() {
            return isgood;
        }

        public void setIsgood(int isgood) {
            this.isgood = isgood;
        }

        public String getRuusername() {
            return ruusername;
        }

        public void setRuusername(String ruusername) {
            this.ruusername = ruusername;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
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
            dest.writeString(this.userheadphoto);
            dest.writeString(this.guajianurl);
            dest.writeString(this.replyfirst);
            dest.writeString(this.createtime);
            dest.writeInt(this.replyCount);
            dest.writeInt(this.isgood);
            dest.writeInt(this.isBest ? 1 : 0);
            dest.writeInt(this.showHeadr ? 1 : 0);
        }

        public RowsBean() {
        }

        protected RowsBean(Parcel in) {
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
            this.userheadphoto = in.readString();
            this.guajianurl = in.readString();
            this.replyfirst = in.readString();
            this.createtime = in.readString();
            this.replyCount = in.readInt();
            this.isgood = in.readInt();
            this.isBest = (in.readInt() == 1) ? true : false;
            this.showHeadr = (in.readInt() == 1) ? true : false;
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
}
