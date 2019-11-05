package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class NoticeHeaderFragment extends BaseStateFragment<MessageDataBean.RowsBean> implements
        BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {
    static String mType = "4";

    /**
     * 用switch的方式会有bug
     * Attempt to invoke virtual method 'int java.lang.String.hashCode()' on a null object reference
     *
     * @return
     */
    @Override
    protected BaseQuickAdapter getAdapter() {
        BaseQuickAdapter adapter;
        if (TextUtils.equals(mType, HelperForStartActivity.KEY_NOTICE_AT_AND_COMMENT)) {
            adapter = new NoticeCommentAdapter();
        } else if (TextUtils.equals(mType, HelperForStartActivity.KEY_NOTICE_FOLLOW)) {
            adapter = new NoticeFollowAdapter();
        } else if (TextUtils.equals(mType, HelperForStartActivity.KEY_NOTICE_LIKE)) {
            adapter = new NoticeLikeAdapter();
        } else {
            adapter = new NoticeOfficialAdapter();
        }
        return adapter;
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
    }

    /**
     * item 里的子控件点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (view.getId() == R.id.iv_notice_user) {
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.friendid);
        } else {
            onItemClick(adapter, view, position);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //2评论3关注4通知5点赞折叠
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (TextUtils.equals("3", content.notetype)) {
            //通知类型,还得判断是否是可以跳转类型,不然就是没有点击事件
            return;
        }

        if (TextUtils.equals("1", content.contentstatus)) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        //新加的通知类型跳转内容详情和web
        if (TextUtils.equals("4", content.notetype)) {
            if (content.content != null && !TextUtils.isEmpty(content.contentid)) {
                if (TextUtils.equals("5", content.content.getContenttype())) {
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(content.content.getContenturllist());
                    HelperForStartActivity.checkUrlForSkipWeb("详情", webList.info, AndroidInterface.type_recommend);
                } else {
                    HelperForStartActivity.openContentDetail(content.contentid);
                }
            }
            return;
        }

        //通知作用对象：1_作品 2_评论
        if (TextUtils.equals("2", content.noteobject)) {
            /**
             * 当notetype=6时，为@消息：
             * 1、当noteobject=1时，点击跳转内容详情；
             * 2、当noteobject=2时，判断commentreply，commentreply=1时，跳转内容详情；commentreply=0时，跳转评论详情
             */
            if (TextUtils.equals("6", content.notetype)
                    && TextUtils.equals("1", content.commentreply)) {
                skipContent(content);
            } else {
                skipComment(content);
            }
        } else {
            skipContent(content);
        }
    }

    public void skipComment(MessageDataBean.RowsBean content) {
        CommendItemBean.RowsBean comment = content.comment;
        if (comment == null || TextUtils.isEmpty(comment.commentid)) {
            ToastUtil.showShort("该评论已删除");
            return;
        }
        comment.setShowContentFrom(true);
        comment.fromCommentId = content.objectid;
        HelperForStartActivity.openCommentDetail(comment);

    }

    public void skipContent(MessageDataBean.RowsBean content) {
        if (content.content == null || TextUtils.isEmpty(content.contentid)) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }

        if (TextUtils.equals("5", content.content.getContenttype())) {
            CommentUrlBean webList = VideoAndFileUtils.getWebList(content.content.getContenturllist());
            HelperForStartActivity.checkUrlForSkipWeb("详情", webList.info, AndroidInterface.type_recommend);
        } else {
            content.content.fromCommentId = content.objectid;
            HelperForStartActivity.openContentDetail(content.content);
        }
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    String mFriendId;

    public void setDate(String type, String friendId) {
        if (!TextUtils.isEmpty(type)) {
            mType = type;
        }
        mFriendId = friendId;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("notetype", mType);
        map.put("friendid", mFriendId);
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_OF_ME)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        DataTransformUtils.changeMsgBean(data.rows);
                        setDate(load_more, data.rows);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MessageDataBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tongzhi;
    }

    @Override
    public String getEmptyText() {
        return "空空如也，快去和段友们互动吧";
    }
}