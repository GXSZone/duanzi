package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
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
 * 用两套布局,不用动态适配的方案,一个影响性能(固定大小可以减少测量次数),一个图标V 还是需要动态改
 * 可以再xml 里设置使用哪套布局 layout_mode
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
        attributeSet = attrs;
        initView(context);
    }

    AttributeSet attributeSet;

    public AvatarWithNameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attributeSet = attrs;
        initView(context);
    }

    /**
     * 这两个参数是在attr 配置的value
     *
     * @param context
     */
    private void initView(Context context) {
        int layout = R.layout.layout_avatar_with_name;
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.AvatarWithNameLayout);
            int mode = typedArray.getInt(R.styleable.AvatarWithNameLayout_layout_mode, 11);
            if (mode == 22) {
                layout = R.layout.layout_avatar_with_name_big;
            }
            typedArray.recycle();
            attributeSet = null;
        }

        LayoutInflater.from(context).inflate(layout, this);
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
        //gone 会导致约束失效
        if (TextUtils.isEmpty(url2)) {
            headgearView.setVisibility(INVISIBLE);
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
