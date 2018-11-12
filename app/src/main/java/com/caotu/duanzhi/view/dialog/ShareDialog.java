package com.caotu.duanzhi.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caotu.duanzhi.R;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 分享弹窗
 */
public class ShareDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    /**
     * 微信好友
     */
    private TextView mShareWeixin;
    /**
     * 朋友圈
     */
    private TextView mShareFriend;
    /**
     * QQ好友
     */
    private TextView mShareQq;
    /**
     * QQ空间
     */
    private TextView mShareQqSpace;
    /**
     * 微博
     */
    private TextView mShareWeibo;
    /**
     * 收藏
     */
    private TextView mShareCollection;
    /**
     * 保存至相册
     */
    private TextView mShareDownloadVideo;
    /**
     * 取消
     */
    private TextView mTvClickCancel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.layout_share_dialog, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mShareWeixin = (TextView) inflate.findViewById(R.id.share_weixin);
        mShareWeixin.setOnClickListener(this);
        mShareFriend = (TextView) inflate.findViewById(R.id.share_friend);
        mShareFriend.setOnClickListener(this);
        mShareQq = (TextView) inflate.findViewById(R.id.share_qq);
        mShareQq.setOnClickListener(this);
        mShareQqSpace = (TextView) inflate.findViewById(R.id.share_qq_space);
        mShareQqSpace.setOnClickListener(this);
        mShareWeibo = (TextView) inflate.findViewById(R.id.share_weibo);
        mShareWeibo.setOnClickListener(this);
        mShareCollection = (TextView) inflate.findViewById(R.id.share_collection);
        mShareCollection.setOnClickListener(this);
        mShareDownloadVideo = (TextView) inflate.findViewById(R.id.share_download_video);
        mShareDownloadVideo.setOnClickListener(this);
        mTvClickCancel = (TextView) inflate.findViewById(R.id.tv_click_cancel);
        mTvClickCancel.setOnClickListener(this);
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_weixin:
                break;
            case R.id.share_friend:
                break;
            case R.id.share_qq:
                break;
            case R.id.share_qq_space:
                break;
            case R.id.share_weibo:
                break;
            case R.id.share_collection:
                break;
            case R.id.share_download_video:
                break;
            case R.id.tv_click_cancel:
                break;
        }
        dismiss();
    }

}
