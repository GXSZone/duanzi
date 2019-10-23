package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;

/**
 * 统一头像处理控件,包含头像,头套,认证标识
 */
public class AvatarWithNameLayout extends FrameLayout {

    private RImageView rImageView;
    private GlideImageView headgearView, authImage;
    private TextView tvUserName, tvUserAuthText;

    public AvatarWithNameLayout(@NonNull Context context) {
        super(context);
        initView(context);

    }

    public AvatarWithNameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initView(context);
    }

    public AvatarWithNameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_avatar_with_name, this);
        rImageView = findViewById(R.id.avatar_iv);
        headgearView = findViewById(R.id.iv_user_headgear);
        authImage = findViewById(R.id.iv_user_auth);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserAuthText = findViewById(R.id.tv_user_auth);
    }

    /**
     * 设置用户边上的文本信息
     *
     * @param userName
     * @param userDes
     */
    public void setUserText(String userName, String userDes) {
        if (TextUtils.isEmpty(userName)) {
            tvUserName.setVisibility(GONE);
        } else {
            tvUserName.setVisibility(VISIBLE);
            tvUserName.setText(userName);
        }
        if (TextUtils.isEmpty(userDes)) {
            tvUserAuthText.setVisibility(GONE);
        } else {
            tvUserAuthText.setVisibility(VISIBLE);
            tvUserAuthText.setText(userDes);
        }
    }

    /**
     * @param url1 头像链接
     * @param url2 头套链接
     * @param url3 认证标识,就是之前名字旁边的标识
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
