package com.caotu.duanzhi.module.search;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public class SearchContentFragment extends BaseVideoFragment implements
        BaseQuickAdapter.OnItemClickListener, SearchDate, IEmpty {
    String searchWord;
    private String searchId;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter() {
            @Override
            public SpannableStringBuilder getText(String contentText) {
                SpannableStringBuilder text = ParserUtils.htmlToSpanText(contentText, true);
                return ParserUtils.setMarkContentText(text, searchWord);
            }
        };
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        if (load_more != DateState.load_more) {
            mStatesView.setCurrentState(StateView.STATE_LOADING);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("pageno", searchId);
        params.put("pagesize", pageSize);
        params.put("querystr", searchWord);

        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.SEARCH_CONTENT)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        searchId = response.body().getData().searchid;
                        List<MomentsDataBean> contentList = response.body().getData().getContentList();
                        if (!AppUtil.listHasDate(contentList)) {
                            mRvContent.setBackgroundColor(DevicesUtils.getColor(R.color.white));
                        } else {
                            mRvContent.setBackgroundColor(Color.parseColor("#f5f6f8"));
                        }
                        setDate(load_more, contentList);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        //注意这里把loading 状态当初始化布局
        mStatesView.setCurrentState(StateView.STATE_EMPTY);
    }

    @Override
    public void setDate(String trim) {
        if (TextUtils.equals(searchWord, trim)) return;
        searchWord = trim;
        if (TextUtils.isEmpty(searchWord)) return;
        searchId = null;
        getNetWorkDate(DateState.init_state);
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    public String getEmptyText() {
        return "找了又找，还是没找到相关内容";
    }

    @Override
    public void changeEmpty() {
        mStatesView.setCurrentState(StateView.STATE_EMPTY);
    }

    @Override
    public void resetSearchWord() {
        searchWord = null;
    }
}
