package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

/**
 * 文本展示和点击事件都在该类处理
 */
public class EyeTopicTextView extends RTextView {

    boolean hasDrawable;

    public EyeTopicTextView(Context context) {
        super(context);
    }

    public EyeTopicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private String topicId;

    public void setTopicText(String tagshowid, String text) {
        if (TextUtils.isEmpty(tagshowid) || TextUtils.isEmpty(text)) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        setText(String.format("#%s#  |", text));
        topicId = tagshowid;
        Drawable[] drawables = getCompoundDrawables();
        if (drawables.length > 2) {
            hasDrawable = !(drawables[2] == null);
        }
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasDrawable && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            boolean isEye = rect.contains(eventX, eventY);
//            if (rect.contains(eventX, eventY)) {
//                // TODO: 2019-10-25 点击插眼图标
//                Log.i("####", "onTouchEvent: 点击插眼图标");
//            } else {
//                Log.i("####", "onTouchEvent: 点击话题");
//            }
            //这个话题请求会有延迟,所以可能需要外部跳转传
            if (isEye) {
                CommonHttpRequest.getInstance().<String>requestFocus(topicId, "1", true,
                        new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {

                            }
                        });
            }
            HelperForStartActivity.openTopicDetailByFollow(topicId, isEye);
        }
        return true;
    }
}
