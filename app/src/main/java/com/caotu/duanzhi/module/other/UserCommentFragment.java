package com.caotu.duanzhi.module.other;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.CommentAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

public class UserCommentFragment extends BaseStateFragment<CommentBaseBean.RowsBean> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @Override
    protected BaseQuickAdapter getAdapter() {
        return new CommentAdapter();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        //position已在父类处理
        map.put("pageno", "" + position);
        map.put("pagesize", pageSize);
        map.put("userid", userId);
        OkGo.<BaseResponseBean<CommentBaseBean>>
                post(HttpApi.USER_MY_COMMENT)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<CommentBaseBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommentBaseBean>> response) {
                        setDate(load_more, response.body().getData().getRows());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<CommentBaseBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
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
        super.initViewListener();
        if (adapter != null) {
            adapter.setOnItemClickListener(this);
            adapter.setOnItemChildClickListener(this);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommentBaseBean.RowsBean bean = (CommentBaseBean.RowsBean) adapter.getData().get(position);
        //0_正常 1_已删除 2_审核中
        skip(bean);
    }

    private void skip(CommentBaseBean.RowsBean bean) {
        if ("1".equals(bean.contentstatus)) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        if (TextUtils.equals("1", bean.commentreply)) {
            //回复的是内容,跳转到内容详情
            if (bean.content == null || TextUtils.isEmpty(bean.contentid)) {
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            // TODO: 2019/4/15 添加评论id标注,这样跳转就能根据id匹配
            if (MySpUtils.isMe(userId)) {
                bean.content.fromCommentId = bean.commentid;
            }
            HelperForStartActivity.openContentDetail(bean.content);
        } else {
            //回复的是评论,跳转到评论详情
            CommendItemBean.RowsBean comment = bean.parentComment;
            if (comment == null || TextUtils.isEmpty(comment.commentid)) {
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            comment.setShowContentFrom(true);
            if (MySpUtils.isMe(userId)) {
                comment.fromCommentId = bean.commentid;
            }
            HelperForStartActivity.openCommentDetail(comment);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        CommentBaseBean.RowsBean bean = (CommentBaseBean.RowsBean) adapter.getData().get(position);
        String commentid = bean.commentid;
        if (view.getId() == R.id.iv_delete_my_post) {
            BaseIOSDialog dialog = new BaseIOSDialog(getContext(), new BaseIOSDialog.SimpleClickAdapter() {
                @Override
                public void okAction() {
                    requestDeleteComment(commentid);
                    adapter.remove(position);
                }
            });
            dialog.setTitleText("是否删除该评论").show();
        } else if (view.getId() == R.id.ll_reply) {
            skip(bean);
        }
    }

    private void requestDeleteComment(String commentid) {
        CommonHttpRequest.getInstance().deleteComment(commentid, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                ToastUtil.showShort("删除成功");
            }
        });
    }

    String userId;

    public void setDate(String myId) {
        userId = myId;
    }
}
