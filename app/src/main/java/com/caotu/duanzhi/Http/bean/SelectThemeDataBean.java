package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/17 16:15
 */
public class SelectThemeDataBean {

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
         * tagid : 158c
         * tagname : 搞笑
         * taglist : []
         */

        private int pageno;
        private int pagesize;
        private int start;
        private String tagid;
        private String tagname;
        private List<TagsBean> taglist;

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

        public String getTagname() {
            return tagname;
        }

        public void setTagname(String tagname) {
            this.tagname = tagname;
        }

        public List<TagsBean> getTaglist() {
            return taglist;
        }

        public void setTaglist(List<TagsBean> taglist) {
            this.taglist = taglist;
        }

        @Override
        public String toString() {
            return "RowsBean{" +
                    "pageno=" + pageno +
                    ", pagesize=" + pagesize +
                    ", start=" + start +
                    ", tagid='" + tagid + '\'' +
                    ", tagname='" + tagname + '\'' +
                    ", taglist=" + taglist +
                    '}';
        }

        public static class TagsBean {
            private String tagalias;
            private String tagid;
            private String tagimg;
            //参与人数
            private String activecount;

            public String getActivecount() {
                return activecount;
            }

            public void setActivecount(String activecount) {
                this.activecount = activecount;
            }

            public String getTagalias() {
                return tagalias;
            }

            public void setTagalias(String tagalias) {
                this.tagalias = tagalias;
            }

            public String getTagid() {
                return tagid;
            }

            public void setTagid(String tagid) {
                this.tagid = tagid;
            }

            public String getTagimg() {
                return tagimg;
            }

            public void setTagimg(String tagimg) {
                this.tagimg = tagimg;
            }

            @Override
            public String toString() {
                return "TagsBean{" +
                        "tagalias='" + tagalias + '\'' +
                        ", tagid='" + tagid + '\'' +
                        ", tagimg='" + tagimg + '\'' +
                        '}';
            }
        }
    }
}
