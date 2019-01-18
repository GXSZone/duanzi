package com.caotu.duanzhi.module.mine.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.module.mine.CommentAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.SpacesItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class MyCommentFragment extends BaseStateFragment<CommentBaseBean.RowsBean> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @Override
    protected BaseQuickAdapter getAdapter() {
        return new CommentAdapter();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        //position已在父类处理
        map.put("pageno", "" + position);
        map.put("pagesize", pageSize);
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

    int mScrollY = 0;
    int headerHeight = 200;
    private TextView titleView;

    @Override
    protected void initViewListener() {
        super.initViewListener();
        mRvContent.addItemDecoration(new SpacesItemDecoration(DevicesUtils.dp2px(5)));
        if (adapter != null) {
            adapter.setOnItemClickListener(this);
            adapter.setOnItemChildClickListener(this);
        }
        titleView = null;
        if (getActivity() != null && getActivity() instanceof BaseBigTitleActivity) {
            titleView = ((BaseBigTitleActivity) getActivity()).getmText();
        }
        View inflate = LayoutInflater.from(mRvContent.getContext()).inflate(R.layout.layout_header_title, mRvContent, false);
        TextView mText = inflate.findViewById(R.id.tv_base_title);
        mText.setText(titleView.getText());
        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        adapter.setHeaderView(inflate);
        adapter.setHeaderAndEmpty(true);
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
                if (dy == 0 || mScrollY > headerHeight) return;
                float scrollY = Math.min(headerHeight, mScrollY);
                float percent = scrollY / headerHeight;
                percent = Math.min(1, percent);
                if (titleView != null) {
                    titleView.setAlpha(percent);
                }
            }
        });
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
            MomentsDataBean beanComment = bean.content;
            if (bean.content == null || TextUtils.isEmpty(bean.contentid)) {
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            HelperForStartActivity.openContentDetail(beanComment, false);
        } else {
            //回复的是评论,跳转到评论详情
            CommendItemBean.RowsBean comment = bean.parentComment;
            if (comment == null || TextUtils.isEmpty(comment.commentid)) {
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            comment.setShowContentFrom(true);
            HelperForStartActivity.openCommentDetail(comment);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        CommentBaseBean.RowsBean bean = (CommentBaseBean.RowsBean) adapter.getData().get(position);
        String commentid = bean.commentid;
        if (view.getId() == R.id.iv_delete_my_post) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("是否删除该评论");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestDeleteComment(commentid);
                    adapter.remove(position);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
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
}
