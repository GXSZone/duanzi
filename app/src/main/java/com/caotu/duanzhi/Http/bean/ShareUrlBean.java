package com.caotu.duanzhi.Http.bean;

public class ShareUrlBean {
    /**
     * url : http://v1.toutushare.com:8838/share/shareindex.html
     */
//	内容分享链接
    public String url;
    //	话题分享链接
    public String t_url;
    //安卓特定url,跟ios区分   内容分享链接
    public String az_url;
    //	评论分享链接
    public String cmt_url;

    public String gohot;    //	上推荐资格 1_有资格 0_无资格	string
    public String youngmod; //	青少年模式开关 1_打开 0_关闭	string
    public String youngpsd; //	青少年模式密码	string
    public String sisword;   //	敏感词 逗号隔开		皮小妹,皮皮,皮皮虾,皮皮搞笑 Sensitive
    public String daily_url; //用户链接
    public String nhsqrz_url; //内含认证Url
}
