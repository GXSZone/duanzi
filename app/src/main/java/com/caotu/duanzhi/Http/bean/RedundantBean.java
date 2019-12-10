package com.caotu.duanzhi.Http.bean;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/9
 * @describe 内容列表为了剥离外层的多余字段, MomentsDataBean不写成内部类的形式, 这样就能内容展示列表用同一个了(这招高明)
 */
public class RedundantBean {


    /**
     * count : 1
     * pageno : 1
     * pagesize : 20
     * rows : [{"bestmap":{"commentgood":"测试内容7gq3","commentid":"测试内容9sm6","commenttext":"测试内容g031","userheadphoto":"测试内容4346","userid":"测试内容v62l","username":"测试内容gy41"},"contentcomment":18818,"contentgood":20,"contentid":"aaa","contentstatus":"测试内容14j3","contenttitle":"哈哈哈哈来看来看","contenttype":"1","contentuid":"测试内容48ik","contenturllist":["1","2","3"],"isfollow":"测试内容3opz","pushcount":0,"readcount":0,"repeatcount":0,"tagshow":"测试内容85ph","tagshowid":"测试内容1l68","userheadphoto":"touxiang.jpg","username":"徐华星01"}]
     */

    public String count;
    public String code;
    public String pageno;
    public String pagesize;
    public String searchid;
    //这里搞两个集合是因为接口字段不一样,照着接口文档的字段按需取就行
    private List<MomentsDataBean> contentList;
    private List<MomentsDataBean> rows;

    public List<MomentsDataBean> getRows() {
        return rows;
    }

    public void setRows(List<MomentsDataBean> rows) {
        this.rows = rows;
    }

    public List<MomentsDataBean> getContentList() {
        return contentList;
    }

    public void setContentList(List<MomentsDataBean> contentList) {
        this.contentList = contentList;
    }

}
