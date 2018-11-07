package com.caotu.duanzhi.Http.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有内容列表的展示对象
 */

public class MomentsDataBean implements Parcelable {


    /**
     * count : 1
     * pageno : 1
     * pagesize : 20
     * rows : [{"bestmap":{"commentgood":"测试内容gm8k","commentid":"测试内容vc87","commenttext":"测试内容54r3","userheadphoto":"测试内容p99o","userid":"测试内容07i8","username":"测试内容uqb6"},"contentcomment":11327,"contentgood":20,"contentid":"aaa","contentstatus":"测试内容97nv","contenttitle":"哈哈哈哈来看来看","contenttype":"1","contentuid":"测试内容thps","contenturllist":["1","2","3"],"isfollow":"测试内容f17g","pushcount":0,"readcount":0,"repeatcount":0,"tagshow":"测试内容4837","tagshowid":"测试内容2x3d","userheadphoto":"touxiang.jpg","username":"徐华星01"}]
     */

    private int count;
    private int pageno;
    private int pagesize;
    private List<RowsBean> rows;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean implements Parcelable {
        /**
         * bestmap : {"commentgood":"测试内容gm8k","commentid":"测试内容vc87","commenttext":"测试内容54r3","userheadphoto":"测试内容p99o","userid":"测试内容07i8","username":"测试内容uqb6"}
         * contentcomment : 11327
         * contentgood : 20
         * contentid : aaa
         * contentstatus : 测试内容97nv
         * contenttitle : 哈哈哈哈来看来看
         * contenttype : 1
         * contentuid : 测试内容thps
         * contenturllist : ["1","2","3"]
         * isfollow : 测试内容f17g
         * pushcount : 0
         * readcount : 0
         * repeatcount : 0
         * tagshow : 测试内容4837
         * tagshowid : 测试内容2x3d
         * userheadphoto : touxiang.jpg
         * username : 徐华星01
         */

        private BestmapBean bestmap;
        private int contentcomment;
        private int contentgood;
        private String contentid;
        private String contentstatus;
        private String contenttitle;
        private String contenttype;
        private String contentuid;
        private String isfollow;
        private int pushcount;
        private int readcount;
        private int repeatcount;
        private String tagshow;
        private String tagshowid;
        private String userheadphoto;
        private String username;
        private List<String> contenturllist;

        public BestmapBean getBestmap() {
            return bestmap;
        }

        public void setBestmap(BestmapBean bestmap) {
            this.bestmap = bestmap;
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

        public String getContentstatus() {
            return contentstatus;
        }

        public void setContentstatus(String contentstatus) {
            this.contentstatus = contentstatus;
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

        public String getIsfollow() {
            return isfollow;
        }

        public void setIsfollow(String isfollow) {
            this.isfollow = isfollow;
        }

        public int getPushcount() {
            return pushcount;
        }

        public void setPushcount(int pushcount) {
            this.pushcount = pushcount;
        }

        public int getReadcount() {
            return readcount;
        }

        public void setReadcount(int readcount) {
            this.readcount = readcount;
        }

        public int getRepeatcount() {
            return repeatcount;
        }

        public void setRepeatcount(int repeatcount) {
            this.repeatcount = repeatcount;
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

        public List<String> getContenturllist() {
            return contenturllist;
        }

        public void setContenturllist(List<String> contenturllist) {
            this.contenturllist = contenturllist;
        }

        public static class BestmapBean implements Parcelable {
            /**
             * commentgood : 测试内容gm8k
             * commentid : 测试内容vc87
             * commenttext : 测试内容54r3
             * userheadphoto : 测试内容p99o
             * userid : 测试内容07i8
             * username : 测试内容uqb6
             */

            private String commentgood;
            private String commentid;
            private String commenttext;
            private String userheadphoto;
            private String userid;
            private String username;

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
            }

            public BestmapBean() {
            }

            protected BestmapBean(Parcel in) {
                this.commentgood = in.readString();
                this.commentid = in.readString();
                this.commenttext = in.readString();
                this.userheadphoto = in.readString();
                this.userid = in.readString();
                this.username = in.readString();
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.bestmap, flags);
            dest.writeInt(this.contentcomment);
            dest.writeInt(this.contentgood);
            dest.writeString(this.contentid);
            dest.writeString(this.contentstatus);
            dest.writeString(this.contenttitle);
            dest.writeString(this.contenttype);
            dest.writeString(this.contentuid);
            dest.writeString(this.isfollow);
            dest.writeInt(this.pushcount);
            dest.writeInt(this.readcount);
            dest.writeInt(this.repeatcount);
            dest.writeString(this.tagshow);
            dest.writeString(this.tagshowid);
            dest.writeString(this.userheadphoto);
            dest.writeString(this.username);
            dest.writeStringList(this.contenturllist);
        }

        public RowsBean() {
        }

        protected RowsBean(Parcel in) {
            this.bestmap = in.readParcelable(BestmapBean.class.getClassLoader());
            this.contentcomment = in.readInt();
            this.contentgood = in.readInt();
            this.contentid = in.readString();
            this.contentstatus = in.readString();
            this.contenttitle = in.readString();
            this.contenttype = in.readString();
            this.contentuid = in.readString();
            this.isfollow = in.readString();
            this.pushcount = in.readInt();
            this.readcount = in.readInt();
            this.repeatcount = in.readInt();
            this.tagshow = in.readString();
            this.tagshowid = in.readString();
            this.userheadphoto = in.readString();
            this.username = in.readString();
            this.contenturllist = in.createStringArrayList();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeInt(this.pageno);
        dest.writeInt(this.pagesize);
        dest.writeList(this.rows);
    }

    public MomentsDataBean() {
    }

    protected MomentsDataBean(Parcel in) {
        this.count = in.readInt();
        this.pageno = in.readInt();
        this.pagesize = in.readInt();
        this.rows = new ArrayList<RowsBean>();
        in.readList(this.rows, RowsBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<MomentsDataBean> CREATOR = new Parcelable.Creator<MomentsDataBean>() {
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
