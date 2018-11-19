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

        public int pageno;
        public int pagesize;
        public int start;
        public String commentid;
        public String userid;
        public String contentid;
        public String commentreply;
        public String commenttext;
        public String replycomment;
        public int commentgood;
        public String username;
        public String userheadphoto;
        public String guajianurl;
        public String replyfirst;
        public String createtime;
        public String isgood;
        public MomentsDataBean content;
        public String parenttext;
        public String parentid;
        public String parentname;
        public String parentphoto;
        public String contentstatus;
        public String commenturl;

    }
}
