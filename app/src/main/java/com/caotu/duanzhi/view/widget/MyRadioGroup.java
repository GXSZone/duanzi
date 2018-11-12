package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.utils.MySpUtils;

/**
 * @author mac
 * @日期: 2018/11/8
 * @describe 内部处理未登录状态的跳转
 */
public class MyRadioGroup extends RadioGroup {
    private final String TAG = "laocuo";

    private int checkId;


    public MyRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false)) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                RadioButton child = (RadioButton) getChildAt(i);
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int left = child.getLeft();
                int right = child.getRight();
                int top = child.getTop();
                int bottom = child.getBottom();
//                Log.d(TAG, "x=" + x + "|x=" + x);
//                Log.d(TAG, "left=" + left + "|right=" + right);
//                Log.d(TAG, "top=" + top + "|bottom=" + bottom);
                if (x < right && x > left && y > top && y < bottom) {
                    checkId = child.getId();
                    break;
                }
            }
            //这个是为了过滤已经选中的状态,再次点击就失去效果
            if (getCheckedRadioButtonId() != checkId) {
                Intent intent = new Intent();
                intent.setClass(MyApplication.getInstance().getRunningActivity(), LoginAndRegisterActivity.class);
                MyApplication.getInstance().getRunningActivity().startActivity(intent);
            }
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
