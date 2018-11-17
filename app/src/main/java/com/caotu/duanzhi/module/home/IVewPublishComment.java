package com.caotu.duanzhi.module.home;

import android.view.View;
import android.widget.EditText;

import com.caotu.duanzhi.Http.bean.CommendItemBean;

/**
 * @author mac
 * @日期: 2018/11/16
 * @describe TODO
 */
public interface IVewPublishComment {
    EditText getEditView();
    View getPublishView();
    void startPublish();
    void publishError();

    // TODO: 2018/11/16 区别于内容发布,这里返回的是bean对象,插入评论列表
    void endPublish(CommendItemBean.RowsBean bean);
}
