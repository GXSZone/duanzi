package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/19 17:52
 */
public class MessageDataBean {


    /**
     * rows : [{"pageno":1,"pagesize":20,"start":0,"noteid":"1e0ad34d7d534058b3b2ab1cda12f441","userid":"f69c7f6d12dd4921abd55f683444fe29","notetype":"1","friendid":"7705d184564644be8dc97ad0debf9c0b","contentid":"f9c3afa68cb641c38cbc920b6a1147d6","noteobject":"1","createtime":"20180719175134","readflag":"1","friendname":"闸弄口韩庚","friendphoto":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/041F4840-F9EE-48CD-A13F-34A0F4902229.png","isfollow":"0","content":{"pageno":1,"pagesize":20,"start":0,"contentid":"f9c3afa68cb641c38cbc920b6a1147d6","contenttitle":"饿得厉害了喇叭","contenttype":"3","contenturllist":"[\"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/1c91bd60-2767-4a7c-88b0-baaf22967434.jpg\"]","contentgood":1,"contenttext":"","contentuid":"f69c7f6d12dd4921abd55f683444fe29","contenttag":"48cd","contentlevel":"3","createtime":"20180719171217","ugc":"1","contentcomment":1,"pushcount":0,"readcount":0,"repeatcount":0,"username":"图友86023475","userheadphoto":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg","isfollow":"0","tagshow":"逆天仿妆术","tagshowid":"48cd"},"contentstatus":"0"},{"pageno":1,"pagesize":20,"start":0,"noteid":"875e6b7f3d1844fb993ee309caae17e8","userid":"f69c7f6d12dd4921abd55f683444fe29","notetype":"1","friendid":"f69c7f6d12dd4921abd55f683444fe29","commentid":"3a0580e3c1774317a0189904284af823","noteobject":"2","createtime":"20180719165348","readflag":"1","friendname":"图友86023475","friendphoto":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg","commenttext":"红红火火恍恍惚惚","isfollow":"0"},{"pageno":1,"pagesize":20,"start":0,"noteid":"24fddf1373d349c8a5a81329c7b4d4bb","userid":"f69c7f6d12dd4921abd55f683444fe29","notetype":"1","friendid":"f69c7f6d12dd4921abd55f683444fe29","commentid":"c28485afdd1848b2b223d1bf782cf49d","noteobject":"2","createtime":"20180719145915","readflag":"1","friendname":"图友86023475","friendphoto":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg","commenttext":"噢噢噢哦哦噢噢噢哦哦","isfollow":"0"}]
     * pageno : 1
     * pagesize : 20
     * count : 3
     */

    private int pageno;
    private int pagesize;
    private int count;
    private List<RowsBean> rows;

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

    public static class RowsBean {
        /**
         * pageno : 1
         * pagesize : 20
         * start : 0
         * noteid : 1e0ad34d7d534058b3b2ab1cda12f441
         * userid : f69c7f6d12dd4921abd55f683444fe29
         * notetype : 1
         * friendid : 7705d184564644be8dc97ad0debf9c0b
         * contentid : f9c3afa68cb641c38cbc920b6a1147d6
         * noteobject : 1
         * createtime : 20180719175134
         * readflag : 1
         * friendname : 闸弄口韩庚
         * friendphoto : http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/041F4840-F9EE-48CD-A13F-34A0F4902229.png
         * isfollow : 0
         * content : {"pageno":1,"pagesize":20,"start":0,"contentid":"f9c3afa68cb641c38cbc920b6a1147d6","contenttitle":"饿得厉害了喇叭","contenttype":"3","contenturllist":"[\"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/1c91bd60-2767-4a7c-88b0-baaf22967434.jpg\"]","contentgood":1,"contenttext":"","contentuid":"f69c7f6d12dd4921abd55f683444fe29","contenttag":"48cd","contentlevel":"3","createtime":"20180719171217","ugc":"1","contentcomment":1,"pushcount":0,"readcount":0,"repeatcount":0,"username":"图友86023475","userheadphoto":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg","isfollow":"0","tagshow":"逆天仿妆术","tagshowid":"48cd"}
         * contentstatus : 0
         * commentid : 3a0580e3c1774317a0189904284af823
         * commenttext : 红红火火恍恍惚惚
         */

        private int pageno;
        private int pagesize;
        private int start;
        private String noteid;
        private String userid;
        private String notetype;
        private String friendid;
        private String contentid;
        private String noteobject;
        private String createtime;
        private String readflag;
        private String friendname;
        private String friendphoto;
        private String guajianurl;
        private String isfollow;
        private ContentBean content;
        private String contentstatus;
        private String commentid;
        private String commenttext;

        public String getGuajianurl() {
            return guajianurl;
        }

        public void setGuajianurl(String guajianurl) {
            this.guajianurl = guajianurl;
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

        public String getNoteid() {
            return noteid;
        }

        public void setNoteid(String noteid) {
            this.noteid = noteid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getNotetype() {
            return notetype;
        }

        public void setNotetype(String notetype) {
            this.notetype = notetype;
        }

        public String getFriendid() {
            return friendid;
        }

        public void setFriendid(String friendid) {
            this.friendid = friendid;
        }

        public String getContentid() {
            return contentid;
        }

        public void setContentid(String contentid) {
            this.contentid = contentid;
        }

        public String getNoteobject() {
            return noteobject;
        }

        public void setNoteobject(String noteobject) {
            this.noteobject = noteobject;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getReadflag() {
            return readflag;
        }

        public void setReadflag(String readflag) {
            this.readflag = readflag;
        }

        public String getFriendname() {
            return friendname;
        }

        public void setFriendname(String friendname) {
            this.friendname = friendname;
        }

        public String getFriendphoto() {
            return friendphoto;
        }

        public void setFriendphoto(String friendphoto) {
            this.friendphoto = friendphoto;
        }

        public String getIsfollow() {
            return isfollow;
        }

        public void setIsfollow(String isfollow) {
            this.isfollow = isfollow;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public String getContentstatus() {
            return contentstatus;
        }

        public void setContentstatus(String contentstatus) {
            this.contentstatus = contentstatus;
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

        public static class ContentBean {
            /**
             * pageno : 1
             * pagesize : 20
             * start : 0
             * contentid : f9c3afa68cb641c38cbc920b6a1147d6
             * contenttitle : 饿得厉害了喇叭
             * contenttype : 3
             * contenturllist : ["https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/1c91bd60-2767-4a7c-88b0-baaf22967434.jpg"]
             * contentgood : 1
             * contenttext :
             * contentuid : f69c7f6d12dd4921abd55f683444fe29
             * contenttag : 48cd
             * contentlevel : 3
             * createtime : 20180719171217
             * ugc : 1
             * contentcomment : 1
             * pushcount : 0
             * readcount : 0
             * repeatcount : 0
             * username : 图友86023475
             * userheadphoto : https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/bce9c251-1266-413e-9b8a-38926f5fb23c.jpg
             * isfollow : 0
             * tagshow : 逆天仿妆术
             * tagshowid : 48cd
             */

            private int pageno;
            private int pagesize;
            private int start;
            private String contentid;
            private String contenttitle;
            private String contenttype;
            private String contenturllist;
            private int contentgood;
            private String contenttext;
            private String contentuid;
            private String contenttag;
            private String contentlevel;
            private String createtime;
            private String ugc;
            private int contentcomment;
            private int pushcount;
            private int readcount;
            private int repeatcount;
            private String username;
            private String userheadphoto;
            private String isfollow;
            private String tagshow;
            private String tagshowid;

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

            public String getContentid() {
                return contentid;
            }

            public void setContentid(String contentid) {
                this.contentid = contentid;
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

            public String getContenturllist() {
                return contenturllist;
            }

            public void setContenturllist(String contenturllist) {
                this.contenturllist = contenturllist;
            }

            public int getContentgood() {
                return contentgood;
            }

            public void setContentgood(int contentgood) {
                this.contentgood = contentgood;
            }

            public String getContenttext() {
                return contenttext;
            }

            public void setContenttext(String contenttext) {
                this.contenttext = contenttext;
            }

            public String getContentuid() {
                return contentuid;
            }

            public void setContentuid(String contentuid) {
                this.contentuid = contentuid;
            }

            public String getContenttag() {
                return contenttag;
            }

            public void setContenttag(String contenttag) {
                this.contenttag = contenttag;
            }

            public String getContentlevel() {
                return contentlevel;
            }

            public void setContentlevel(String contentlevel) {
                this.contentlevel = contentlevel;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getUgc() {
                return ugc;
            }

            public void setUgc(String ugc) {
                this.ugc = ugc;
            }

            public int getContentcomment() {
                return contentcomment;
            }

            public void setContentcomment(int contentcomment) {
                this.contentcomment = contentcomment;
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

            public String getIsfollow() {
                return isfollow;
            }

            public void setIsfollow(String isfollow) {
                this.isfollow = isfollow;
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
        }
    }
}
