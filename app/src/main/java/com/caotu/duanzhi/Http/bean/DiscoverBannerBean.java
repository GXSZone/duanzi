package com.caotu.duanzhi.Http.bean;

import java.util.List;

public class DiscoverBannerBean {

    private List<BannerListBean> bannerList;

    public List<BannerListBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerListBean> bannerList) {
        this.bannerList = bannerList;
    }

    public static class BannerListBean {
        /**
         * bannerdesc : 解锁月相，发现未知的自己
         * bannerid : s815
         * bannerpic : https://ctkj-1256675270.file.myqcloud.com/zhongqiujie_yuexiang_banner_01.png
         * bannersharepic : https://ctkj-1256675270.file.myqcloud.com/zhongqiu_yuexiang_sharepic_01.png
         * bannertext : 解锁月相，发现未知的自己
         * bannertype : 1
         * bannerurl : https://cdn.toutushare.com/activity_zhongqiu_v2/active.html?appevent=banner
         */

        public String bannerdesc;
        public String bannerid;
        public String bannerpic;
        public String bannersharepic;
        public String bannertext;
        public String bannertype;
        public String bannerurl;
    }

}
