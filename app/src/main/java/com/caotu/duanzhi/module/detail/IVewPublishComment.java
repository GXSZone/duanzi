package com.caotu.duanzhi.module.detail;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.module.publish.IVewPublish;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/16
 * @describe TODO
 */
public interface IVewPublishComment extends IVewPublish {
    void publishError();

    // TODO: 2018/11/16 区别于内容发布,这里返回的是bean对象,插入评论列表
    void endPublish(CommendItemBean.RowsBean bean);

    void publishCantTalk(String msg);

    void uploadProgress(int progress);

    void setListDate(List<CommendItemBean.RowsBean> listDate, @DateState int load_more);
}
