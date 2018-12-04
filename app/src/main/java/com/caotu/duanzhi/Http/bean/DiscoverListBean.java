package com.caotu.duanzhi.Http.bean;

import java.util.List;

public class DiscoverListBean {

    private List<RowsBean> rows;

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean {
        /**
         * isfollow : 测试内容91nv
         * tagalias : 测试4
         * tagid : 1076
         * tagimg : http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/klgdijypbrjlol2y80.jpg
         * taglead : yyy
         */

        public String isfollow;
        public String tagalias;
        public String tagid;
        public String tagimg;
        public String taglead;

    }

}
