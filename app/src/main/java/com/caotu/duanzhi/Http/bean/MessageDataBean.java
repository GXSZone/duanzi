package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/19 17:52
 */
public class MessageDataBean {

    /**
     * count : 1
     * pageno : 1
     * pagesize : 20
     * rows : [{"commentid":"87fyddvdv","commenttext":"duiduidui","content":{"contentcomment":"测试内容0x69","contentgood":"测试内容lw89","contentid":"测试内容8oo3","contenttext":"测试内容l82l","contenttitle":"测试内容uh37","contenttype":"测试内容s9f1","contenturllist":"测试内容l1g8","isfollow":"测试内容4o23","tagshow":"测试内容122v","tagshowid":"测试内容ktwj"},"contentstatus":"测试内容3912","createtime":"20180526","friendid":"sdfhu","friendidArray":["string1","string2","string3","string4","string5"],"friendname":"测试内容8vug","friendnameArray":["string1","string2","string3","string4","string5"],"friendphoto":"测试内容kjdf","friendphotoArray":["string1","string2","string3","string4","string5"],"isfollow":"测试内容6fll","noteid":"sfasfasf8","noteobject":"2","notetext":"测试内容27y0","notetype":"2","readflag":"0","userid":"f12f790jffhsidh"}]
     */

    public int count;
    public int pageno;
    public int pagesize;
    public List<RowsBean> rows;

    public static class RowsBean {
        /**
         * commentid : 87fyddvdv
         * commenttext : duiduidui
         * content : {"contentcomment":"测试内容0x69","contentgood":"测试内容lw89","contentid":"测试内容8oo3","contenttext":"测试内容l82l","contenttitle":"测试内容uh37","contenttype":"测试内容s9f1","contenturllist":"测试内容l1g8","isfollow":"测试内容4o23","tagshow":"测试内容122v","tagshowid":"测试内容ktwj"}
         * contentstatus : 测试内容3912
         * createtime : 20180526
         * friendid : sdfhu
         * friendidArray : ["string1","string2","string3","string4","string5"]
         * friendname : 测试内容8vug
         * friendnameArray : ["string1","string2","string3","string4","string5"]
         * friendphoto : 测试内容kjdf
         * friendphotoArray : ["string1","string2","string3","string4","string5"]
         * isfollow : 测试内容6fll
         * noteid : sfasfasf8
         * noteobject : 2
         * notetext : 测试内容27y0
         * notetype : 2
         * readflag : 0
         * userid : f12f790jffhsidh
         */

        public String commentid;
        public String commenttext;
        public ContentBean content;
        public String contentstatus;
        public String createtime;
        public String friendid;
        public String friendname;
        public String friendphoto;
        public String isfollow;
        public String noteid;
        public String noteobject;
        public String notetext;
        public String notetype;
        public String readflag;
        public String userid;
        public List<String> friendidArray;
        public List<String> friendnameArray;
        public List<String> friendphotoArray;

        public static class ContentBean {
            /**
             * contentcomment : 测试内容0x69
             * contentgood : 测试内容lw89
             * contentid : 测试内容8oo3
             * contenttext : 测试内容l82l
             * contenttitle : 测试内容uh37
             * contenttype : 测试内容s9f1
             * contenturllist : 测试内容l1g8
             * isfollow : 测试内容4o23
             * tagshow : 测试内容122v
             * tagshowid : 测试内容ktwj
             */

            public String contentcomment;
            public String contentgood;
            public String contentid;
            public String contenttext;
            public String contenttitle;
            public String contenttype;
            public String contenturllist;
            public String isfollow;
            public String tagshow;
            public String tagshowid;
        }
    }

}
