package com.caotu.duanzhi.Http.bean;

/**
 * 所有内容列表的展示对象
 */

public class MomentsDataBean {

    /**
     * bestmap : [{"commentgood":1,"commentid":1,"commenttext":1,"userheadphoto":1,"userid":1,"username":1}]
     * contentbad : 17553
     * contentcomment : 0
     * contentgood : 1
     * contentid : 73d6592a600b11e8afee309c23a27303
     * contentlevel : 3
     * contenttag : 8c5f;5283;8yhn;
     * contenttext :
     * contenttitle : #宠物##猫星人##心都被萌化了#高冷p总的反差萌
     * contenttype : 1
     * contentuid : ae007cd921884577a0726bc495ff102c
     * contenturllist : ["https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/e51960f78dfd49b79aacd439d012fefd.jpg","https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/c4d9e2e972f649a9b139b19bfa5f486f.mp4"]
     * createtime : 20180531074647
     * isfollow : N
     * isshowtitle : 1
     * playcount : 47477
     * pushcount : 0
     * readcount : 0
     * repeatcount : 0
     * showtime : 1
     * tagshow : 1
     * tagshowid : 测试内容ih18
     * userheadphoto : 1
     * username : 1
     */

    private int contentbad;
    private int contentcomment;
    private int contentgood;
    private String contentid;
    private String contentlevel;
    private String contenttag;
    private String contenttext;
    private String contenttitle;
    private String contenttype;
    private String contentuid;
    private String createtime;
    private String isfollow;
    private String isshowtitle;
    //这些数值类型都用string接收,以防解析出错
    private String playcount;
    private String pushcount;
    private String readcount;
    private String repeatcount;
    private String showtime;
    private String tagshow;
    private String tagshowid;
    private String userheadphoto;
    private String username;
    private BestmapBean bestmap;
    // TODO: 2018/11/9 到底是list 还是 string 接收还是个问题
    private String contenturllist;
    //自己添加的字段,用来记录是否点过赞或者踩过,接口不记录   默认值0, 1代表点过赞,2代表踩过
    private int hasLikeOrUnlike;

    public BestmapBean getBestmap() {
        return bestmap;
    }

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public String getPushcount() {
        return pushcount;
    }

    public void setPushcount(String pushcount) {
        this.pushcount = pushcount;
    }

    public String getReadcount() {
        return readcount;
    }

    public void setReadcount(String readcount) {
        this.readcount = readcount;
    }

    public String getRepeatcount() {
        return repeatcount;
    }

    public void setRepeatcount(String repeatcount) {
        this.repeatcount = repeatcount;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }

    public void setBestmap(BestmapBean bestmap) {
        this.bestmap = bestmap;
    }

    public int getHasLikeOrUnlike() {
        return hasLikeOrUnlike;
    }

    public void setHasLikeOrUnlike(int hasLikeOrUnlike) {
        this.hasLikeOrUnlike = hasLikeOrUnlike;
    }

    public int getContentbad() {
        return contentbad;
    }

    public void setContentbad(int contentbad) {
        this.contentbad = contentbad;
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

    public String getContentlevel() {
        return contentlevel;
    }

    public void setContentlevel(String contentlevel) {
        this.contentlevel = contentlevel;
    }

    public String getContenttag() {
        return contenttag;
    }

    public void setContenttag(String contenttag) {
        this.contenttag = contenttag;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getIsfollow() {
        return isfollow;
    }

    public void setIsfollow(String isfollow) {
        this.isfollow = isfollow;
    }

    public String getIsshowtitle() {
        return isshowtitle;
    }

    public void setIsshowtitle(String isshowtitle) {
        this.isshowtitle = isshowtitle;
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


    public String getContenturllist() {
        return contenturllist;
    }

    public void setContenturllist(String contenturllist) {
        this.contenturllist = contenturllist;
    }

    public static class BestmapBean  {
        /**
         * commentgood : 1
         * commentid : 1
         * commenttext : 1
         * userheadphoto : 1
         * userid : 1
         * username : 1
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

    }

}
