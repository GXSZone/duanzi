package com.caotu.duanzhi.view.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 分享弹窗
 * 分享弹窗逻辑是:先在外部判断好是否显示收藏和视频下载按钮的显示,弹窗内部只处理赋值分享的平台,真正唤起三方分享在sharehelp里实现
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
    //分享内容的对象
    private WebShareBean bean;

    public static ShareDialog newInstance(WebShareBean bean) {
        final ShareDialog fragment = new ShareDialog();
        final Bundle args = new Bundle();
        args.putParcelable("bean", bean);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDate();
        View inflate = inflater.inflate(R.layout.layout_share_dialog, container, false);
        initView(inflate);
        return inflate;
    }

    private void getDate() {
        if (getArguments() != null) {
            bean = getArguments().getParcelable("bean");
        }
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
        //只有内容列表和内容详情的分享视频才有下载
        mShareDownloadVideo.setVisibility(bean == null || !bean.isVideo
                || TextUtils.isEmpty(bean.VideoUrl)
                ? View.GONE : View.VISIBLE);
        //只有内容列表才有这个展示
        mShareCollection.setVisibility(bean == null || !bean.isNeedShowCollection
                || TextUtils.isEmpty(bean.contentId)
                ? View.GONE : View.VISIBLE);
        mShareCollection.setText(bean.hasColloection ? "取消收藏" : "收藏");
        if (mShareDownloadVideo.getVisibility() == View.GONE && mShareCollection.getVisibility() == View.GONE) {
            inflate.findViewById(R.id.space).setLayoutParams(new LinearLayout.LayoutParams(0, 1, 3));
        } else if (mShareDownloadVideo.getVisibility() == View.GONE || mShareCollection.getVisibility() == View.GONE) {
            inflate.findViewById(R.id.space).setLayoutParams(new LinearLayout.LayoutParams(0, 1, 2));
        }


    }

    @Override
    public void onClick(View v) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        switch (v.getId()) {
            case R.id.share_weixin:
                bean.medial = SHARE_MEDIA.WEIXIN;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_friend:
                bean.medial = SHARE_MEDIA.WEIXIN_CIRCLE;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_qq:
                bean.medial = SHARE_MEDIA.QQ;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_qq_space:
                bean.medial = SHARE_MEDIA.QZONE;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_weibo:
                bean.medial = SHARE_MEDIA.SINA;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_collection:
                if (!MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, LoginAndRegisterActivity.class);
                    activity.startActivityForResult(intent, LoginAndRegisterActivity.LOGIN_REQUEST_CODE);
                    return;
                }
                if (!TextUtils.isEmpty(bean.contentId)) {
                    CommonHttpRequest.getInstance().collectionContent(bean.contentId, !bean.hasColloection, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            if (listener != null) {
                                listener.colloection(!bean.hasColloection);
                            }
                        }
                    });
                }
                break;
            case R.id.share_download_video:
                //处理视频下载一块
                VideoAndFileUtils.checkNetwork(activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Activity.RESULT_OK == which) {
                            // TODO: 2018/11/13 需要优化下载队列
                            ToastUtil.showShort("正在下载中");
                            if (TextUtils.isEmpty(bean.VideoUrl)) return;
                            String end = bean.VideoUrl.substring(bean.VideoUrl.lastIndexOf("."),
                                    bean.VideoUrl.length());
                            String fileName = "duanzi-" + System.currentTimeMillis() + end;
                            OkGo.<File>get(bean.VideoUrl)
                                    .execute(new FileCallback(PathConfig.VIDEO_PATH, fileName) {
                                        @Override
                                        public void onSuccess(Response<File> response) {
                                            ToastUtil.showShort(R.string.video_save_success);
                                            File body = response.body();
                                            //通知系统相册更新
                                            MyApplication.getInstance().getRunningActivity().
                                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                            Uri.fromFile(body)));
                                        }
                                    });
                        }
                    }
                });

                break;
            case R.id.tv_click_cancel:
                break;
        }
        dismiss();
    }

    public ShareMediaCallBack listener;

    /**
     * 必须调用回调方法
     *
     * @param listener
     */
    public void setListener(ShareMediaCallBack listener) {
        this.listener = listener;
    }

    public interface ShareMediaCallBack {
        void callback(WebShareBean bean);

        void colloection(boolean isCollection);
    }

}
