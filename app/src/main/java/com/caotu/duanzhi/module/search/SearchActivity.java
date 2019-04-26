package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.ruffian.library.widget.REditText;

public class SearchActivity extends BaseActivity {

    private REditText mEtSearchUser;
    private SearchFragment fragment;

    @Override
    protected void initView() {
        mEtSearchUser = findViewById(R.id.et_search_user);
        findViewById(R.id.tv_click_back).setOnClickListener(v -> finish());
        mEtSearchUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String trim = mEtSearchUser.getText().toString().trim();
                    if (TextUtils.isEmpty(trim)) {
                        ToastUtil.showShort("请先输入搜索内容");
                        return false;
                    } else {
                        if (fragment != null) {
                            fragment.setDate(trim);
                        }
                    }
                }
                return false;
            }
        });
        fragment = new SearchFragment();
        turnToFragment(null, fragment, R.id.fl_fragment_content);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_search;
    }
}
