package com.caotu.duanzhi.module.home;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/21 14:06
 */

public class ContentAdapter extends BaseQuickAdapter<CommendItemBean.RowsBean,BaseViewHolder> {
    public ContentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommendItemBean.RowsBean item) {

    }
//    private List<CommendDataBean.RowsBean> bestlist;
//    private List<CommendDataBean.RowsBean> rows;
//    private LayoutInflater layoutInflater;
//    private CustomInformDialog customInformDialog;
////    private String parentCommendName = "";
//    private BindPhoneDialog bindPhoneDialog;
//    private String myId;
//
//    CommendDataBean.RowsBean firstSPL;
//
//    public ContentAdapter() {
//        super(layoutResId);
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, CommendItemBean.RowsBean item) {
//
//    }
//
//    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        final CommendDataBean.RowsBean dataItem;
//        int bestSize = 0;
//        if (bestlist != null) {
//            bestSize = bestlist.size();
//        }
//
//
//        //判断是不是神评论
//        boolean best = dataItem.isBest();
//        boolean showHeadr = dataItem.isShowHeadr();
//        holder.splIv.setVisibility(best ? View.VISIBLE : View.GONE);
//        holder.headrLayout.setVisibility(showHeadr ? View.VISIBLE : View.GONE);
//        holder.tail.setVisibility(dataItem.isHideTail() ? View.GONE : View.VISIBLE);
//        if (showHeadr) {
//            holder.headrTx.setText(best ? "热门评论" : "图友哔哔区");
//        }
//
//        holder.avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(App.getInstance().getRunningActivity(), VisitOtherActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("type", "other");
//                bundle.putString("id", dataItem.getUserid());
//                intent.putExtras(bundle);
//                App.getInstance().getRunningActivity().startActivity(intent);
//            }
//        });
//
//        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickItemListener.onClickItem(dataItem);
//            }
//        });
//        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                customInformDialog.setData(dataItem.getContentid(), 2);
//                customInformDialog.setOnClickListener(DetailsCommentAdapter.this);
//                customInformDialog.show();
//                return false;
//            }
//        });
//
//        final Map<String, Boolean> isPariseMap = App.getInstance().getIsPariseMap();
//        String commentid = dataItem.getCommentid();
//        boolean containsKey = isPariseMap.containsKey(commentid);
//        boolean isGood = containsKey ? isPariseMap.get(commentid) : false;
//        dataItem.setIsgood(isGood ? 1 : 0);
//        holder.praiseIv.setSelected(isGood);
//        holder.praiselayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String commentid = dataItem.getCommentid();
//                boolean containsKey = App.getInstance().getIsPariseMap().containsKey(commentid);
//                if (!containsKey) {
//                    AnimationUtils.startLikeAnimation(holder.praiseIv);
//                    requestParise(holder.praiseIv, holder.praiseTx, dataItem, dataItem.getUserid(), commentid);
//                }
//            }
//        });
//
//        holder.commentTx.setText(dataItem.getCommenttext());
//        holder.nameTx.setText(dataItem.getUsername());
//        holder.praiseTx.setText(Int2TextUtils.toText(dataItem.getCommentgood(), "W"));
//
//        holder.commentCountTx.setText(dataItem.getReplyCount() == 0 ? "" : "共有" + dataItem.getReplyCount() + "条回复");
//        holder.commentCountTx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(App.getInstance().getRunningActivity(), CommendDetailsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("commend", dataItem);
//                intent.putExtras(bundle);
//                Activity runningActivity = App.getInstance().getRunningActivity();
//                runningActivity.startActivity(intent);
//                runningActivity.overridePendingTransition(R.anim.input_method_enter, 0);
//            }
//        });
//        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final CustomShareDialog customShareDialog = new CustomShareDialog(App.getInstance().getRunningActivity(), true);
//                customShareDialog.setIsVisible(View.GONE);
//                customShareDialog.setData(ShareToutuNewActivity.TYPE_COMMEND, dataItem.getContentid(), dataItem.getUserheadphoto(), dataItem.getGuajianurl(), dataItem.getUsername(), dataItem.getCommenttext());
//                customShareDialog.show();
//            }
//        });
//
//        if (position == 0 && best) {
//            firstSPL = dataItem;
//            if (onChangeSplListener != null) {
//                onChangeSplListener.onChangeSpl(firstSPL);
//            }
//        }
//    }
//
////    public void setTextClick(TextView tv, String content, int start, int end, ClickableSpan clickableSpan) {
////        final SpannableStringBuilder style = new SpannableStringBuilder();
////        //设置文字
////        style.append(content);
////        style.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        tv.setText(style);
////        //设置部分文字颜色
////        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(App.getInstance().getResources().getColor(R.color.color_pinkFF698F));
////        style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        //配置给TextView
////        tv.setMovementMethod(LinkMovementMethod.getInstance());
////        tv.setText(style);
////    }
//
//
//
//
//
//    public void requestParise( String id, final String contentId) {
//
//        CommonHttpRequest.getInstance().requestCommentsLike();
//
//
//        String stringBody = RequestUtils.getRequestBody(map);
//        VolleyRequest.RequestPostJsonObjectApp(true, App.getInstance().getRunningActivity(), HTTPAPI.PARISE, stringBody, null, new VolleyJsonObjectInterface() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                Log.i("TAG", "response:" + response.toString());
//                String code = response.optString("code");
//                if ("1000".equals(code)) {
//                    view.setSelected(true);
//                    int commentgood = rowsBean.getCommentgood() + 1;
//                    rowsBean.setCommentgood(commentgood);
//                    //处理评论是否超过1000
//                    handleBestList(commentgood);
//                    tv.setText(Int2TextUtils.toText(commentgood, "W"));
//                    App.getInstance().AddIsPariseItem(rowsBean.getCommentid(), true);
//                    rowsBean.setIsgood(1);
//                    return;
//                }
//                ToastUtil.showShort("点赞失败！");
//            }
//
//        });
//    }


}
