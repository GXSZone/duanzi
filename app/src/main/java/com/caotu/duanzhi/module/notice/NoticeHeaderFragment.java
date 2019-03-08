package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
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
    String mType;

    @Override
    protected BaseQuickAdapter getAdapter() {
        BaseQuickAdapter adapter;
        switch (mType) {
            case HelperForStartActivity.KEY_NOTICE_COMMENT:
                adapter = new NoticeCommentAdapter();
                break;
            case HelperForStartActivity.KEY_NOTICE_FOLLOW:
                adapter = new NoticeFollowAdapter();
                break;
            case HelperForStartActivity.KEY_NOTICE_LIKE:
                adapter = new NoticeLikeAdapter(null);
                break;
            default:
                adapter = new NoticeOfficialAdapter();
                break;
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
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (TextUtils.equals("3", content.notetype) || TextUtils.equals("4", content.notetype)) {
            //该类型是关注
            return;
        }
        //2评论3关注4通知5点赞折叠
        if ("1".equals(content.contentstatus)) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        // TODO: 2018/12/12 剩下类型为2,5评论和点赞的跳转
        //通知作用对象：1_作品 2_评论
        if (TextUtils.equals("2", content.noteobject)) {
            CommendItemBean.RowsBean comment = content.comment;
            if (comment == null || TextUtils.isEmpty(comment.commentid)) {
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            comment.setShowContentFrom(true);
            HelperForStartActivity.openCommentDetail(comment);
        } else {
            if (content.content == null || TextUtils.isEmpty(content.content.getContentid())) {
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            HelperForStartActivity.openContentDetail(content.content, false);
        }
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String type) {
        mType = type;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("notetype", mType);
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_OF_ME)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
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