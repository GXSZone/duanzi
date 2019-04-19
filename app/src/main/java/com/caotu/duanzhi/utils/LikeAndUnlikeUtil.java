package com.caotu.duanzhi.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.caotu.duanzhi.R;
import com.sum.slike.BitmapProvider;
import com.sum.slike.SuperLikeLayout;

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
    public static void showLike(View locationView, int x, int y) {
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
//        ImageView likeView = new ImageView(context);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DevicesUtils.dp2px(18),
//                DevicesUtils.dp2px(18));
//        likeView.setImageResource(R.drawable.shenpin_dianzan_pressed);
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_anim_likeview, null);
        int[] outLocation = new int[2];
        locationView.getLocationInWindow(outLocation);
//        // 不同的需求可以自己测出需要的偏移量
//        layoutParams.leftMargin = outLocation[0] + x;
//        layoutParams.topMargin = outLocation[1] + y;
// TODO: 2019/4/19 参考动画:https://github.com/Qiu800820/SuperLike
        BitmapProvider.Provider provider = new BitmapProvider.Builder(locationView.getContext())
                .setDrawableArray(new int[]{R.mipmap.dianzan_mao})
                .build();
        SuperLikeLayout likeLayout = inflate.findViewById(R.id.super_like);
        likeLayout.setProvider(provider);
        likeLayout.launch(outLocation[0] + x, outLocation[1] + y);

//        likeView.setLayoutParams(layoutParams);
        frameLayout.addView(inflate);
//        likeView.animate().scaleXBy(2.0f).scaleYBy(2.0f).alpha(0).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                ViewGroup parent = (ViewGroup) likeView.getParent();
//                if (parent != null) {
//                    parent.removeView(likeView);
//                }
//            }
//        });
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
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                ToastUtil.showShort("动画结束");
                        ViewGroup parent = (ViewGroup) noticeTipView.getParent();
                        if (parent != null) {
                            parent.removeView(noticeTipView);
                        }
                    }
                });
    }
}
