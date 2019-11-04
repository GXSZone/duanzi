package com.caotu.duanzhi.Http.bean;

public class SplashBean {


    public String thumbnail;
    public String wap_url;
    public String showtime;
    public String sharepic;
    public String sharetext;
    public AndroidAdBean androidAd;

    public static class AndroidAdBean {
        /**
         * loc_banner : 测试内容6so5
         * loc_comment : 测试内容ntxd
         * loc_content : 测试内容h4ry
         * loc_screem : 测试内容0u80
         * loc_table : 测试内容i834
         */

        public String loc_banner;     //        loc_banner  Banner广告 1_开启 0_关闭	string
        public String loc_comment;    //        loc_comment	评论广告 1_开启 0_关闭	string
        public String loc_content;    //        loc_content	内容广告 1_开启 0_关闭	string
        public String loc_screem;     //        loc_screem	开屏广告 1_开启 0_关闭	string
        public String loc_table;      //        loc_table	首页tab分栏广告 1_开启 0_关闭	string
        public String loc_table_pic;
        public String loc_table_text;
        public String loc_table_video;
    }

}
