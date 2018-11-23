package com.caotu.duanzhi.Http.bean;

public class ShareUrlBean {
    /**
     * url : http://v1.toutushare.com:8838/share/shareindex.html
     */
//	内容分享链接
    private String url;
    //	话题分享链接
    private String t_url;
    //安卓特定url,跟ios区分   内容分享链接
    private String az_url;
    //	评论分享链接
    private String cmt_url;

    public String getAz_url() {
        return az_url;
    }

    public void setAz_url(String az_url) {
        this.az_url = az_url;
    }

    public String getCmt_url() {
        return cmt_url;
    }

    public void setCmt_url(String cmt_url) {
        this.cmt_url = cmt_url;
    }

    public String getT_url() {
        return t_url;
    }

    public void setT_url(String t_url) {
        this.t_url = t_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
