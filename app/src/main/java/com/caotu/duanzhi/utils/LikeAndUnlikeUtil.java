package com.caotu.duanzhi.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.caotu.duanzhi.R;

/**
 * @author mac
 * @日期: 2018/11/14
 * @describe TODO
 */
public class LikeAndUnlikeUtil {
    /**
     * 判断是否已经点了赞,针对只有一个💕点赞按钮
     *
     * @param goodstatus
     * @return
     */
    public static boolean isLiked(String goodstatus) {
        return TextUtils.equals("1", goodstatus);
    }

    public static boolean isVideoType(String type) {
        return TextUtils.equals("1", type) || TextUtils.equals("2", type);
    }

    /**
     * 显示点赞动画
     * 借鉴来源:https://blog.csdn.net/qq_22706515/article/details/80691971
     *
     * @param locationView 需要定位的view，动画将显示在其左下方
     */
    static long time;

    public static void showLike(View locationView) {
        if (locationView == null) {
            return;
        }
        long l = System.currentTimeMillis();
        if (l - time < 1100) {
            return;
        }
        time = l;
        Context context = locationView.getContext();
        if (!(context instanceof Activity)) {
            return;
        }
        //1.获取Activity最外层的DecorView
        Activity activity = (Activity) context;
        FrameLayout frameLayout = null;
        try {
            frameLayout = (FrameLayout) activity.getWindow().getDecorView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (frameLayout == null) return;
        int[] outLocation = new int[2];
        locationView.getLocationInWindow(outLocation);
        LottieAnimationView animationView = new LottieAnimationView(locationView.getContext());
        animationView.setImageAssetsFolder("images");

        int height = DevicesUtils.dp2px(100);
        int width = DevicesUtils.dp2px(100);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);

        params.leftMargin = outLocation[0] - 20;
        params.topMargin = (int) (outLocation[1] - height / 1.1f);
        animationView.setLayoutParams(params);
        animationView.setAnimation("like.json");
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup parent = (ViewGroup) animationView.getParent();
                if (parent != null) {
                    parent.removeView(animationView);
                }
            }
        });
        animationView.playAnimation();
        frameLayout.addView(animationView);
    }


    public static void showNoticeTip(View locationView) {
        if (locationView == null) {
            return;
        }
        Context context = locationView.getContext();
        if (!(context instanceof Activity)) {
            return;
        }
        //1.获取Activity最外层的DecorView
        Activity activity = (Activity) context;
        View decorView = activity.getWindow().getDecorView();
        FrameLayout frameLayout = null;
        if (decorView != null && decorView instanceof FrameLayout) {
            frameLayout = (FrameLayout) decorView;
        }
        if (frameLayout == null) {
            return;
        }
        //2.通过getLocationInWindow 获取需要显示的位置
        ImageView noticeTipView = new ImageView(context);
        int width = DevicesUtils.dp2px(52);
        int height = DevicesUtils.dp2px(42);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        noticeTipView.setScaleType(ImageView.ScaleType.CENTER);
        noticeTipView.setImageResource(R.mipmap.tab_newtixin);
        int[] outLocation = new int[2];
        locationView.getLocationInWindow(outLocation);

        layoutParams.leftMargin = outLocation[0] + locationView.getWidth() / 2 - width / 2;
        layoutParams.topMargin = outLocation[1] - height - 20;

        noticeTipView.setLayoutParams(layoutParams);
        frameLayout.addView(noticeTipView);


        noticeTipView.animate().translationYBy(15).setInterpolator(new CycleInterpolator(3.0f))
                .setDuration(3000)
                //还有这个🆕api 都不需要监听动画了
                .withEndAction(() -> {
                    ViewGroup parent = (ViewGroup) noticeTipView.getParent();
                    if (parent != null) {
                        parent.removeView(noticeTipView);
                    }
                });
    }
}
