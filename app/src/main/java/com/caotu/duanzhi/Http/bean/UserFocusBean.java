package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * 该对象包含粉丝和点赞用户所有字段,所以都统一了,覆盖使用之前的 UserFansBean
 * 也就是字段多的覆盖字段少的bean对象使用
 */
public class UserFocusBean {


    /**
     * rows : [{"pageno":1,"pagesize":20,"start":0,"tagid":"3322","taglead":"暴雪娱乐制作的一款大型多人在线角色扮演游戏，探索前所未有无与伦比的史诗级游戏体验！","tagalias":"为了爱泽拉斯","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/fapgj2z7s8ji4c6fih.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"3ba5","taglead":"刺激的\u201c吃鸡\u201d游戏，进入荒野世界，处处都是炫技舞台，玩出你的独门风格！","tagalias":"大吉大利，今晚吃鸡","tagimg":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"48cd","taglead":"这哪里是化妆，简直是换头！","tagalias":"逆天仿妆术","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/2sclpu0kseji4d86oc.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"4b16","taglead":"爱豆同款穿搭，当季最热单品，和爱豆一起美～","tagalias":"明星穿搭","tagimg":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"74c9","taglead":"","tagalias":"0","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/w3q327r31ji4do563.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"7663","taglead":"十四周年宏大的世界观，上古神话背景，酣畅淋漓的战斗PK，带你探险传奇世界，成就一代邪神！","tagalias":"传奇世界视频更新","tagimg":"https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/005e83f15601421ab6297d17921c14ea.jpg","isfollow":"1"},{"pageno":1,"pagesize":20,"start":0,"tagid":"e94c","taglead":"","tagalias":"梦幻西游","tagimg":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/188uzjys99jia3tcpg.jpg","isfollow":"1"}]
     * pageno : 1
     * pagesize : 20
     * count : 7
     */

    public int pageno;
    public int pagesize;
    public int count;
    private List<RowsBean> rows;

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

        public int pageno;
        public int pagesize;
        public int start;
        public String isfollow;
        public String eachotherflag;

        public String tagid;
        public String taglead;
        public String tagalias;
        public String tagimg;

        public String userid;
        public String usersign;
        public String userheadphoto;
        public String username;
        public String uno; //段友号
        public AuthBean auth;
        public String authname;
    }
}
