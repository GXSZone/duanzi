package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;

/**
 * 统一头像处理控件,包含头像,头套,认证标识
 */
public class AvatarLayout extends RelativeLayout {

    private RImageView rImageView;
    private GlideImageView headgearView;
    private GlideImageView authImage;

    public AvatarLayout(@NonNull Context context) {
        super(context);
        initView(context);

    }

    public AvatarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initView(context);
    }

    public AvatarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_avatar, this);
        rImageView = findViewById(R.id.avatar_iv);
        headgearView = findViewById(R.id.iv_user_headgear);
        authImage = findViewById(R.id.iv_user_auth);
    }


    /**
     * @param url1 头像链接
     * @param url2 头套链接
     * @param url3 认证标识
     */
    public void load(String url1, String url2, String url3) {

        if (rImageView == null) return;
        //如果不是约定尺寸则需要动态根据

        if (TextUtils.isEmpty(url1)) {
            rImageView.setImageResource(R.mipmap.touxiang_moren);
        } else {
            GlideUtils.loadImage(url1, rImageView, false);
        }

        if (TextUtils.isEmpty(url2)) {
            headgearView.setVisibility(GONE);
        } else {
            headgearView.setVisibility(VISIBLE);
            headgearView.load(url2);
        }

        if (TextUtils.isEmpty(url3)) {
            authImage.setVisibility(GONE);
        } else {
            authImage.setVisibility(VISIBLE);
            authImage.load(url3);
        }
    }
}
