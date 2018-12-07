package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/12 18:09
 */
public class UserFansBean {

    /**
     * rows : [{"pageno":1,"pagesize":20,"start":0,"userid":"7705d184564644be8dc97ad0debf9c0b","username":"闸弄口韩庚","userheadphoto":"http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/041F4840-F9EE-48CD-A13F-34A0F4902229.png","usertype":"1","eachotherflag":"1","isfollow":"1"}]
     * pageno : 1
     * pagesize : 20
     * count : 1
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
         * userid : 7705d184564644be8dc97ad0debf9c0b
         * username : 闸弄口韩庚
         * userheadphoto : http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/041F4840-F9EE-48CD-A13F-34A0F4902229.png
         * usertype : 1
         * eachotherflag : 1
         * isfollow : 1
         */

        private int pageno;
        private int pagesize;
        private int start;
        private String userid;
        private String username;
        private String userheadphoto;
        private String usertype;
        private String eachotherflag;
        private String isfollow;
        private AuthBean auth;

        public AuthBean getAuth() {
            return auth;
        }

        public void setAuth(AuthBean auth) {
            this.auth = auth;
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

        public String getUserheadphoto() {
            return userheadphoto;
        }

        public void setUserheadphoto(String userheadphoto) {
            this.userheadphoto = userheadphoto;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }

        public String getEachotherflag() {
            return eachotherflag;
        }

        public void setEachotherflag(String eachotherflag) {
            this.eachotherflag = eachotherflag;
        }

        public String getIsfollow() {
            return isfollow;
        }

        public void setIsfollow(String isfollow) {
            this.isfollow = isfollow;
        }
    }
}
