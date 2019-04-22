package com.caotu.duanzhi.module.base;

import android.app.Activity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;

import java.lang.reflect.Field;

/**
 * 侧滑返回的帮助类,还需要设置activity的透明背景
 * https://blog.csdn.net/molashaonian/article/details/51009832
 */
public class SlideCloseHelper {
    private static final SlideCloseHelper ourInstance = new SlideCloseHelper();

    public static SlideCloseHelper getInstance() {
        return ourInstance;
    }

    private SlideCloseHelper() {
    }

    public void initSlideBackClose(Activity activity) {
        //三星去除该操作
        if (DevicesUtils.isSanxing()) return;
        SlidingPaneLayout slidingPaneLayout = new SlidingPaneLayout(activity);
        // 通过反射改变mOverhangSize的值为0，
        // 这个mOverhangSize值为菜单到右边屏幕的最短距离，
        // 默认是32dp，现在给它改成0
        try {
            Field overhangSize = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            overhangSize.setAccessible(true);
            overhangSize.set(slidingPaneLayout, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelOpened(View panel) {
                activity.finish();
            }
        });

        slidingPaneLayout.setSliderFadeColor(DevicesUtils.getColor(R.color.transparent));

        // 左侧的透明视图
        View leftView = new View(activity);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        slidingPaneLayout.addView(leftView, 0);
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();


        // 右侧的内容视图
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        decorView.removeView(decorChild);
        decorView.addView(slidingPaneLayout);

        // 为 SlidingPaneLayout 添加内容视图
        slidingPaneLayout.addView(decorChild, 1);

    }
}
