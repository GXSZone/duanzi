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

            public String contentcomment;
            public String contentgood;
            public String contentid;
            public String contenttext;
            public String contenttitle;
            public String contenttype;
            public String contentuid;
            public String contenturllist;
            public String isfollow;
            public String tagshow;
            public String tagshowid;
            public String userheadphoto;
            public String username;
            public String contentbad;
        }

    }
}
