package com.caotu.duanzhi.view.dialog;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.VideoDownloadHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.luck.picture.lib.tools.StringUtils;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 分享弹窗
 * 分享弹窗逻辑是:先在外部判断好是否显示收藏和视频下载按钮的显示,弹窗内部只处理赋值分享的平台,真正唤起三方分享在sharehelp里实现
 */
public class ShareDialog extends BaseDialogFragment implements View.OnClickListener {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bean = getArguments().getParcelable("bean");
        }
    }

    @Override
    public int getLayout() {
        return R.layout.layout_share_dialog;
    }

    @Override
    protected void initView(View inflate) {
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle);
        inflate.findViewById(R.id.share_weixin).setOnClickListener(this);
        inflate.findViewById(R.id.share_friend).setOnClickListener(this);
        inflate.findViewById(R.id.share_qq).setOnClickListener(this);
        inflate.findViewById(R.id.share_qq_space).setOnClickListener(this);
        inflate.findViewById(R.id.share_weibo).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_cancel).setOnClickListener(this);

        TextView mShareCollection = (TextView) inflate.findViewById(R.id.share_collection);
        mShareCollection.setOnClickListener(this);
        /**
         * 保存至相册
         */
        TextView mShareDownloadVideo = (TextView) inflate.findViewById(R.id.share_download_video);
        mShareDownloadVideo.setOnClickListener(this);

        View copyText = inflate.findViewById(R.id.share_copy_text);
        copyText.setOnClickListener(this);
        copyText.setVisibility(TextUtils.isEmpty(bean.copyText) ? View.GONE : View.VISIBLE);

        //只有内容列表和内容详情的分享视频才有下载
        mShareDownloadVideo.setVisibility(bean == null || !bean.isVideo
                || TextUtils.isEmpty(bean.VideoUrl)
                ? View.GONE : View.VISIBLE);

        if (bean != null && bean.webType == 1) {
            mShareDownloadVideo.setVisibility(View.VISIBLE);
            mShareDownloadVideo.setText("保存图片");
        }
        //只有内容列表才有这个展示
        mShareCollection.setVisibility(bean == null || !bean.isNeedShowCollection
                || TextUtils.isEmpty(bean.contentId)
                ? View.GONE : View.VISIBLE);
        mShareCollection.setText(bean.hasColloection ? "取消收藏" : "收藏");
        if (bean.hasColloection) {
            StringUtils.modifyTextViewDrawable(mShareCollection,
                    DevicesUtils.getDrawable(R.mipmap.share_shoucang_pressed), 1);
        }

        int weight = 2;
        if (mShareDownloadVideo.getVisibility() == View.GONE) {
            weight++;
        }
        if (mShareCollection.getVisibility() == View.GONE) {
            weight++;
        }
        if (copyText.getVisibility() == View.GONE) {
            weight++;
        }
        inflate.findViewById(R.id.space).setLayoutParams(new LinearLayout.LayoutParams(0, 1, weight));

    }

    @Override
    public void onClick(View v) {
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
                if (LoginHelp.isLoginAndSkipLogin()) {
                    if (!TextUtils.isEmpty(bean.contentId)) {
                        final boolean isCollection = !bean.hasColloection;
                        CommonHttpRequest.getInstance().collectionContent(bean.contentId, isCollection, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
//                                StringUtils.modifyTextViewDrawable(mShareCollection,
//                                        DevicesUtils.getDrawable(R.mipmap.share_shoucang_pressed),1);
                                if (listener != null) {
                                    listener.colloection(isCollection);
                                }
                            }
                        });
                    }
                }

                break;
            case R.id.share_download_video:
                if (bean == null) return;
                boolean isPic = bean.webType == 1 && !TextUtils.isEmpty(bean.url);
                VideoDownloadHelper.getInstance().startDownLoad(!isPic, isPic ? null : bean.url,
                        isPic ? bean.url : bean.VideoUrl);
                break;
            case R.id.share_copy_text:
                if (bean == null) return;
                if (!TextUtils.isEmpty(bean.copyText)) {
                    ClipboardManager cm = (ClipboardManager) MyApplication.getInstance().
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(bean.copyText);
                    ToastUtil.showShort("复制成功");
                }
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

    public abstract static class SimperMediaCallBack implements ShareMediaCallBack {

        @Override
        public void colloection(boolean isCollection) {

        }
    }

}
