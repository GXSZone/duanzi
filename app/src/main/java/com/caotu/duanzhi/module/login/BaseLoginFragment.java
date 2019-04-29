package com.caotu.duanzhi.module.login;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.TextWatcherAdapter;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;


/**
 * Created by Administrator on 2018/6/18.
 */

public abstract class BaseLoginFragment extends Fragment implements View.OnClickListener {

    protected View rootView;
    public EditText phoneEdt, passwordEdt;
    public TextView btDone;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getFragmentLayoutId(), container, false);
        initView(rootView);
        return rootView;
    }

    protected abstract @LayoutRes
    int getFragmentLayoutId();

    @CallSuper
    protected void initView(View rootView) {
        phoneEdt = rootView.findViewById(R.id.phone_et);
        passwordEdt = rootView.findViewById(R.id.password_et);
        btDone = rootView.findViewById(R.id.fragment_login_login_but);
        btDone.setOnClickListener(this);
        rootView.findViewById(R.id.tv_user_agreement).setOnClickListener(this);
        initEtlistener();
    }

    protected void initEtlistener() {
        phoneEdt.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (passwordEdt == null || passwordEdt.getText() == null
                        || phoneEdt == null || phoneEdt.getText() == null
                        || TextUtils.isEmpty(content)) {
                    setConfirmButton(false);
                    return;
                }
                boolean firstFinish = getFirstEditStrategy(phoneEdt, content);
                firstListener(firstFinish, content);
                boolean secondFinish = getSecondEditStrategy(passwordEdt, passwordEdt.getText().toString());
                setConfirmButton(firstFinish && secondFinish);
            }
        });

        passwordEdt.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (passwordEdt == null || passwordEdt.getText() == null
                        || phoneEdt == null || phoneEdt.getText() == null
                        || TextUtils.isEmpty(content)) {
                    setConfirmButton(false);
                    return;
                }
                boolean firstFinish = getFirstEditStrategy(phoneEdt, phoneEdt.getText().toString());
                boolean secondFinish = getSecondEditStrategy(passwordEdt, content);
                setConfirmButton(firstFinish && secondFinish);
            }
        });
    }

    /**
     * 用于子类对第一个输入框完成就需要回调的情况
     *
     * @param firstFinish
     * @param content
     */
    public void firstListener(boolean firstFinish, String content) {

    }

    /**
     * 子类处理第二个输入框的处理策略
     *
     * @param phoneEdt
     * @param content
     */
    protected abstract boolean getSecondEditStrategy(EditText phoneEdt, String content);

    /**
     * 子类处理第一个输入框的处理策略
     *
     * @param phoneEdt
     * @param content
     * @return
     */
    public abstract boolean getFirstEditStrategy(EditText phoneEdt, String content);

    /**
     * 父类提供一些输入框的策略,子类直接调用即可
     *
     * @param content
     * @return
     */
    protected boolean passWordStrategy(String content) {
        return (content.length() <= 16 && content.length() >= 6);
    }

    protected boolean phoneStrategy(String content) {
        return (content.length() == 11);
    }

    /**
     * 默认两个输入框的条件都满足
     *
     * @param isComplete
     */
    public void setConfirmButton(boolean isComplete) {

        btDone.setEnabled(isComplete);
    }

    /**
     * 子类可以复写验证规则
     *
     * @param text
     * @return
     */
    protected boolean checkFirstEdit(String text) {
        return ValidatorUtils.isMobile(text);
    }

    public EditText getPhoneEdt() {
        return phoneEdt;
    }

    public EditText getPasswordEdt() {
        return passwordEdt;
    }

    protected boolean checkSecondEdit(String text) {
        return ValidatorUtils.isPassword(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_login_login_but) {
            if (phoneEdt == null || phoneEdt.getText() == null
                    || !checkFirstEdit(phoneEdt.getText().toString())) {
                firstEtDontPass();
                return;
            }
            if (passwordEdt == null || passwordEdt.getText() == null ||
                    !checkSecondEdit(passwordEdt.getText().toString())) {
                secondEtDontPass();
                return;
            }
            doBtClick(v);
        } else if (v.getId() == R.id.tv_user_agreement) {
            WebActivity.openWeb("用户协议", BaseConfig.KEY_USER_AGREEMENT, false);
        }
    }

    /**
     * 用于处理第二个输入框输入没有通过的情况
     */
    protected void secondEtDontPass() {
        ToastUtil.showShort(R.string.password_not_right);
    }

    /**
     * 用于处理第一个输入框输入没有通过的情况
     */
    protected void firstEtDontPass() {
        ToastUtil.showShort(R.string.phone_number_not_right);
    }

    protected abstract void doBtClick(View v);

}