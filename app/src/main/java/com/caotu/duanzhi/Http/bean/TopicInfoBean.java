package com.caotu.duanzhi.Http.bean;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/18 22:58
 */
public class TopicInfoBean {


    /**
     * pageno : 1
     * pagesize : 20
     * start : 0
     * tagid : 3ba5
     * tagpid : 09e8
     * tagname : 荒野行动
     * taglevel : 3
     * onelevel : 17cf
     * twolevel : 09e8
     * createtime : 20180607162905
     * createuser : 2002
     * taglead : 刺激的“吃鸡”游戏，进入荒野世界，处处都是炫技舞台，玩出你的独门风格！
     * tagalias : 大吉大利，今晚吃鸡
     * tagimg : https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg
     * isfollow : 0
     */

    private int pageno;
    private int pagesize;
    private int start;
    private String tagid;
    private String tagpid;
    private String tagname;
    private String taglevel;
    private String onelevel;
    private String twolevel;
    private String createtime;
    private String createuser;
    private String taglead;
    private String tagalias;
    private String tagimg;
    private String isfollow;
    public String activecount; //参与讨论人数
    public MomentsDataBean hotcontent;// 热门内容 字段与其他Content相同


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

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTagpid() {
        return tagpid;
    }

    public void setTagpid(String tagpid) {
        this.tagpid = tagpid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public String getTaglevel() {
        return taglevel;
    }

    public void setTaglevel(String taglevel) {
        this.taglevel = taglevel;
    }

    public String getOnelevel() {
        return onelevel;
    }

    public void setOnelevel(String onelevel) {
        this.onelevel = onelevel;
    }

    public String getTwolevel() {
        return twolevel;
    }

    public void setTwolevel(String twolevel) {
        this.twolevel = twolevel;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public String getTaglead() {
        return taglead;
    }

    public void setTaglead(String taglead) {
        this.taglead = taglead;
    }

    public String getTagalias() {
        return tagalias;
    }

    public void setTagalias(String tagalias) {
        this.tagalias = tagalias;
    }

    public String getTagimg() {
        return tagimg;
    }

    public void setTagimg(String tagimg) {
        this.tagimg = tagimg;
    }

    public String getIsfollow() {
        return isfollow;
    }

    public void setIsfollow(String isfollow) {
        this.isfollow = isfollow;
    }
}
