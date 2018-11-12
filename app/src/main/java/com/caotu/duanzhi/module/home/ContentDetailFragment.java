package com.caotu.duanzhi.module.home;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.MyRadioGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;

import org.json.JSONObject;

import java.util.HashMap;

public class ContentDetailFragment extends BaseStateFragment<MomentsDataBean> {
    public String contentId;
    public String shareUrl;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter();
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_pinlun;
    }

    @Override
    public String getEmptyText() {
        //直接用string形式可以少一步IO流从xml读写
        return "下个神评就是你，快去评论吧";
    }

    @Override
    protected void initViewListener() {
        // TODO: 2018/11/5 初始化头布局
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_detail_header, mRvContent, false);
        initHeaderView(headerView);
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        CommonHttpRequest.getInstance().getShareUrl(contentId, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                shareUrl = response.body().getData().getUrl();
            }
        });
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (DateState.init_state == load_more || DateState.refresh_state == load_more) {
            HashMap<String, String> hashMapParams = new HashMap<>();
            hashMapParams.put("contentid", contentId);
            // TODO: 2018/11/11 头布局和列表展示用同一个bean对象 ,有新字段再加
            OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.WORKSHOW_DETAILS)
                    .upJson(new JSONObject(hashMapParams))
                    .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                            MomentsDataBean data = response.body().getData();
                            bindHeader(data);
                        }
                    });
        }

        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();

        hashMapParams.put("cmttype", "1");//1_一级评论列表 2_子评论列表
        hashMapParams.put("pageno", "" + position);
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("pid", contentId);//cmttype为1时：作品id ; cmttype为2时，评论id

        OkGo.<BaseResponseBean<CommendItemBean>>post(HttpApi.THEME_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<CommendItemBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommendItemBean>> response) {

                    }
                });


    }

    private void bindHeader(MomentsDataBean data) {
        if (viewHolder != null) {
            viewHolder.bindDate(data);
        }
    }

    public void setDate(String id) {
        contentId = id;
    }

    ViewHolder viewHolder;

    public void initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
        }
    }

    /**
     * 针对头布局单独抽出一个类
     */
    public class ViewHolder {
        public View rootView;
        public RImageView mBaseMomentAvatarIv;
        public TextView mBaseMomentNameTv;
        public ImageView mIvIsFollow;
        public TextView mTvContentText;
        public LinearLayout mBaseMomentImgsLl;
        public RadioButton mBaseMomentLike;
        public RadioButton mBaseMomentUnlike;
        public MyRadioGroup mLikeOrUnlikeGroup;
        public TextView mBaseMomentComment;
        public ImageView mBaseMomentShareIv;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mBaseMomentAvatarIv = (RImageView) rootView.findViewById(R.id.base_moment_avatar_iv);
            this.mBaseMomentNameTv = (TextView) rootView.findViewById(R.id.base_moment_name_tv);
            this.mIvIsFollow = (ImageView) rootView.findViewById(R.id.iv_is_follow);
            this.mTvContentText = (TextView) rootView.findViewById(R.id.tv_content_text);
            this.mBaseMomentImgsLl = (LinearLayout) rootView.findViewById(R.id.base_moment_imgs_ll);
            this.mBaseMomentLike = (RadioButton) rootView.findViewById(R.id.base_moment_like);
            this.mBaseMomentUnlike = (RadioButton) rootView.findViewById(R.id.base_moment_unlike);
            this.mLikeOrUnlikeGroup = (MyRadioGroup) rootView.findViewById(R.id.like_or_unlike_group);
            this.mBaseMomentComment = (TextView) rootView.findViewById(R.id.base_moment_comment);
            this.mBaseMomentShareIv = (ImageView) rootView.findViewById(R.id.base_moment_share_iv);
        }

        public void bindDate(MomentsDataBean data) {
            GlideUtils.loadImage(data.getUserheadphoto(), mBaseMomentAvatarIv);

            mBaseMomentNameTv.setText(data.getUsername());
            //1关注 0未关注
            if ("0".equals(data.getIsfollow())) {
                mIvIsFollow.setSelected(false);
            } else {
                mIvIsFollow.setEnabled(false);
            }
            mIvIsFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHttpRequest.getInstance().<String>requestFocus(data.getContentuid(),
                            "2", true, new JsonCallback<BaseResponseBean<String>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<String>> response) {
                                    ToastUtil.showShort("关注成功");
                                    mIvIsFollow.setEnabled(true);
                                }

                                @Override
                                public void onError(Response<BaseResponseBean<String>> response) {
                                    ToastUtil.showShort("关注失败,请稍后重试");
                                    super.onError(response);
                                }
                            });
                }
            });
            // TODO: 2018/11/11 不知道是否要展示话题
            setContentText(mTvContentText, data.getTagshow(), data.getContenttext(), "1".equals(data.getIsshowtitle()), data.getTagshowid());
            mBaseMomentShareIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(shareUrl)) return;
                    ShareDialog shareDialog = new ShareDialog();
                    shareDialog.show(getChildFragmentManager(), "shareDialog");
                }
            });

        }


        /**
         * 处理显示内容
         *
         * @param contentView
         * @param tagshow
         * @param contenttext
         * @param ishowTag
         * @param tagshowid
         */
        private void setContentText(TextView contentView, String tagshow, String contenttext,
                                    boolean ishowTag, String tagshowid) {
            if (ishowTag && !TextUtils.isEmpty(tagshow)) {
                String source = "#" + tagshow + "#" + contenttext;
                SpannableString ss = new SpannableString(source);

                ss.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // TODO: 2018/11/8 话题详情
                        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, tagshowid);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, 0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                        0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                contentView.setText(ss);
                contentView.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                contentView.setText(contenttext);
            }
        }
    }
}
