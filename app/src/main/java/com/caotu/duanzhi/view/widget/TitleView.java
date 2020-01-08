package com.caotu.duanzhi.view.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;


/**
 * 自动播放完成界面
 */
public class TitleView extends LinearLayout implements View.OnClickListener {

    private TextView titleTextView;
    private ImageView backImage,moreView;

    public TitleView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.layout_title_view, this);
        backImage = findViewById(R.id.iv_back);
        backImage.setOnClickListener(this);
        moreView = findViewById(R.id.iv_more_bt);
        moreView.setOnClickListener(this);
        moreView.setColorFilter(DevicesUtils.getColor(R.color.color_FF698F));
        titleTextView = findViewById(R.id.tv_title_big);
    }

    public void setTitleText(CharSequence text) {
        if (titleTextView != null) {
            titleTextView.setText(text);
        }
    }

    public void setRightViewShow(boolean isGone) {
        if (moreView != null) {
            moreView.setVisibility(isGone ? INVISIBLE : VISIBLE);
        }
    }

    public void setMoreView(@DrawableRes int drawable) {
        if (moreView != null) {
            moreView.setImageResource(drawable);
        }
    }

    public void setBackView(@DrawableRes int drawable) {
        if (backImage != null) {
            backImage.setImageResource(drawable);
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    BtClickListener clickListener;

    public void setClickListener(BtClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            Context context = getContext();
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        } else if (i == R.id.iv_more_bt && clickListener != null) {
            clickListener.rightClick();
        }
    }

    public interface BtClickListener {
        void rightClick();
    }
}
