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
        public String contentid; //该字段用来判断当前消息是否展示成跟评论一样的有内容条
        public String commentid;
        public String commenttext;
        //跳转内容详情可以直接用
        public MomentsDataBean content;
        public CommendItemBean.RowsBean comment;
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
        public int friendcount;
        public String guajianurl;
        public String commenturl;

        public String objectid; //关联此消息的评论id
        //当noteobject=2时，判断commentreply，commentreply=1时，跳转内容详情；commentreply=0时，跳转评论详情
        public String commentreply;
        public String authPic;
    }
}
