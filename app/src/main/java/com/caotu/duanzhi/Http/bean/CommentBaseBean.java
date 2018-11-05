package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class CommentBaseBean {


    /**
     * count : 4
     * pageno : 1
     * pagesize : 20
     * rows : [{"content":{"contentcomment":"测试内容3m23","contentgood":"测试内容nc64","contentid":"测试内容y247","contenttext":"测试内容54io","contenttitle":"测试内容61f4","contenttype":"测试内容f55x","contentuid":"测试内容906f","contenturllist":"测试内容od6r","isfollow":"测试内容l2v7","tagshow":"测试内容d7z6","tagshowid":"测试内容met1","userheadphoto":"测试内容15y4","username":"测试内容rxqq"},"contentstatus":"测试内容ihtb","createtime":"测试内容f43v","isgood":"测试内容d12o","parentid":"测试内容k8n1","parentname":"测试内容n455","parenttext":"测试内容1n1m"}]
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

    public static class RowsBean {
        /**
         * content : {"contentcomment":"测试内容3m23","contentgood":"测试内容nc64","contentid":"测试内容y247","contenttext":"测试内容54io","contenttitle":"测试内容61f4","contenttype":"测试内容f55x","contentuid":"测试内容906f","contenturllist":"测试内容od6r","isfollow":"测试内容l2v7","tagshow":"测试内容d7z6","tagshowid":"测试内容met1","userheadphoto":"测试内容15y4","username":"测试内容rxqq"}
         * contentstatus : 测试内容ihtb
         * createtime : 测试内容f43v
         * isgood : 测试内容d12o
         * parentid : 测试内容k8n1
         * parentname : 测试内容n455
         * parenttext : 测试内容1n1m
         */

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
        private String userheadphoto;
        private String guajianurl;
        private String replyfirst;
        private String createtime;
        private String isgood;
        private ContentBean content;
        private String parenttext;
        private String parentid;
        private String parentname;
        private String parentphoto;
        private String contentstatus;

        public String getContentstatus() {
            return contentstatus;
        }

        public void setContentstatus(String contentstatus) {
            this.contentstatus = contentstatus;
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

        public String getIsgood() {
            return isgood;
        }

        public void setIsgood(String isgood) {
            this.isgood = isgood;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public String getParenttext() {
            return parenttext;
        }

        public void setParenttext(String parenttext) {
            this.parenttext = parenttext;
        }

        public String getParentid() {
            return parentid;
        }

        public void setParentid(String parentid) {
            this.parentid = parentid;
        }

        public String getParentname() {
            return parentname;
        }

        public void setParentname(String parentname) {
            this.parentname = parentname;
        }

        public String getParentphoto() {
            return parentphoto;
        }

        public void setParentphoto(String parentphoto) {
            this.parentphoto = parentphoto;
        }

        public static class ContentBean {
            /**
             * contentcomment : 测试内容3m23
             * contentgood : 测试内容nc64
             * contentid : 测试内容y247
             * contenttext : 测试内容54io
             * contenttitle : 测试内容61f4
             * contenttype : 测试内容f55x
             * contentuid : 测试内容906f
             * contenturllist : 测试内容od6r
             * isfollow : 测试内容l2v7
             * tagshow : 测试内容d7z6
             * tagshowid : 测试内容met1
             * userheadphoto : 测试内容15y4
             * username : 测试内容rxqq
             */

            private String contentcomment;
            private String contentgood;
            private String contentid;
            private String contenttext;
            private String contenttitle;
            private String contenttype;
            private String contentuid;
            private String contenturllist;
            private String isfollow;
            private String tagshow;
            private String tagshowid;
            private String userheadphoto;
            private String username;

            public String getContentcomment() {
                return contentcomment;
            }

            public void setContentcomment(String contentcomment) {
                this.contentcomment = contentcomment;
            }

            public String getContentgood() {
                return contentgood;
            }

            public void setContentgood(String contentgood) {
                this.contentgood = contentgood;
            }

            public String getContentid() {
                return contentid;
            }

            public void setContentid(String contentid) {
                this.contentid = contentid;
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

            public String getContenturllist() {
                return contenturllist;
            }

            public void setContenturllist(String contenturllist) {
                this.contenturllist = contenturllist;
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
        }

    }
}
