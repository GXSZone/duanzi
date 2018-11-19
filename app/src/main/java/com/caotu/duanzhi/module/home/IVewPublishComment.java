package com.caotu.duanzhi.module.home;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.module.publish.IVewPublish;

/**
 * @author mac
 * @日期: 2018/11/16
 * @describe TODO
 */
public interface IVewPublishComment extends IVewPublish {
    void publishError();
    // TODO: 2018/11/16 区别于内容发布,这里返回的是bean对象,插入评论列表
    void endPublish(CommendItemBean.RowsBean bean);
}
