package com.caotu.duanzhi.module.publish;

import android.view.View;
import android.widget.EditText;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe TODO
 */
public interface publishView {
    EditText getEditView();
    View getPublishView();
    void startPublish();
//    void publishError();
//    void endPublish();
}
