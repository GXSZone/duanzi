package com.caotu.duanzhi.module.search;

import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.ruffian.library.widget.REditText;

import java.util.List;

public class SearchActivity extends BaseActivity {
    public static final String KEY_TYPE = "TYPE";
    public static final int search_user = 759;
    public static final int search_at_user = 547;
    public static final int select_topic = 761;
    private REditText mEtSearchUser;
    private SearchDate interFace;
    private int intExtra;

    @Override
    protected void initView() {
        mEtSearchUser = findViewById(R.id.et_search_user);
        //限制输入12位
        mEtSearchUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});

        mEtSearchUser.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
            }
            return false;
        });
        intExtra = getIntent().getIntExtra(KEY_TYPE, search_user);
        if (intExtra == search_at_user) {
            mEtSearchUser.setHint("输入段友号或昵称搜索用户");
            turnToFragment(null, new AtUserFragment(), R.id.fl_fragment_content);
        } else if (intExtra == select_topic) {
            mEtSearchUser.setHint("搜索话题");
            interFace = new SelectTopicFragment();
            turnToFragment(null, (BaseFragment) interFace, R.id.fl_fragment_content);
        } else {
            interFace = new SearchParentFragment();
            turnToFragment(null, (BaseFragment) interFace, R.id.fl_fragment_content);
        }
        findViewById(R.id.search_back).setOnClickListener(v -> finish());
        findViewById(R.id.tv_click_search).setOnClickListener(v -> search());
        mEtSearchUser.requestFocus();
    }

    private void search() {
        UmengHelper.event("ssan");
        String trim = mEtSearchUser.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.showShort("请先输入搜索内容");
        } else {
            replaceFragment(trim);
        }
    }

    public EditText getEtSearch() {
        return mEtSearchUser;
    }

    private void replaceFragment(String trim) {
        if (intExtra == search_at_user) {
            addFragment(trim);
        } else if (interFace != null) {
            interFace.setDate(trim);
        }
    }

    boolean isSearchMode = false;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            isSearchMode = false;
            getSupportFragmentManager().popBackStack();
        } else {
            if (!VideoViewManager.instance().onBackPressed()) {
                super.onBackPressed();
            }
        }
    }


    /**
     * 这里有两种情况,一种留在搜索页继续搜索,一种返回后再搜索,fragment处理不一样
     *
     * @param trim
     */
    private void addFragment(String trim) {
        if (interFace != null && isSearchMode) {//保险起见
            interFace.setDate(trim);
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        interFace = new SearchResultFragment();
        interFace.setDate(trim);
        ft.add(R.id.fl_fragment_content, (BaseFragment) interFace);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        ft.hide(fragments.get(0));
        ft.addToBackStack(null);//统计回退栈
        ft.commitAllowingStateLoss();
        isSearchMode = true;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_search;
    }


    /**
     * @param userInfoBean
     */
    public void backSetResult(UserBean userInfoBean) {
        MySpUtils.putAtUserToSp(userInfoBean);
        Intent data = new Intent();
        data.putExtra(HelperForStartActivity.KEY_AT_USER, userInfoBean);
        setResult(RESULT_OK, data);
        finish();
    }
}
