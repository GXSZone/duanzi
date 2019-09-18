package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/12 15:34
 */
public class UserFocusBean {


    /**
     * rows : [{"pageno":1,"pagesize":20,"start":0,"tagid":"3322","taglead":"暴雪娱乐制作的一款大型多人在线角色扮演游戏，探索前所未有无与伦比的史诗级游戏体验！","tagalias":"为了爱泽拉斯","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/fapgj2z7s8ji4c6fih.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"3ba5","taglead":"刺激的\u201c吃鸡\u201d游戏，进入荒野世界，处处都是炫技舞台，玩出你的独门风格！","tagalias":"大吉大利，今晚吃鸡","tagimg":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"48cd","taglead":"这哪里是化妆，简直是换头！","tagalias":"逆天仿妆术","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/2sclpu0kseji4d86oc.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"4b16","taglead":"爱豆同款穿搭，当季最热单品，和爱豆一起美～","tagalias":"明星穿搭","tagimg":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"74c9","taglead":"","tagalias":"0","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/w3q327r31ji4do563.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"7663","taglead":"十四周年宏大的世界观，上古神话背景，酣畅淋漓的战斗PK，带你探险传奇世界，成就一代邪神！","tagalias":"传奇世界视频更新","tagimg":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"e94c","taglead":"","tagalias":"梦幻西游","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/188uzjys99jia3tcpg.jpg","isfollow":"1"}]
     * pageno : 1
     * pagesize : 20
     * count : 7
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
         * tagid : 3322
         * taglead : 暴雪娱乐制作的一款大型多人在线角色扮演游戏，探索前所未有无与伦比的史诗级游戏体验！
         * tagalias : 为了爱泽拉斯
         * tagimg : http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/fapgj2z7s8ji4c6fih.jpg
         * isfollow : 1
         * eachotherflag
         */


        private int pageno;
        private int pagesize;
        private int start;
        private String isfollow;
        private String eachotherflag;

        private String tagid;
        private String taglead;
        private String tagalias;
        private String tagimg;

        private String userid;
        private String usersign;
        private String userheadphoto;
        private String username;
        private String uno; //段友号
        private AuthBean auth;

        public String getUno() {
            return uno;
        }

        public void setUno(String uno) {
            this.uno = uno;
        }

        public AuthBean getAuth() {
            return auth;
        }

        public void setAuth(AuthBean auth) {
            this.auth = auth;
        }

        public String getEachotherflag() {
            return eachotherflag;
        }

        public void setEachotherflag(String eachotherflag) {
            this.eachotherflag = eachotherflag;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsersign() {
            return usersign;
        }

        public void setUsersign(String usersign) {
            this.usersign = usersign;
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
}
