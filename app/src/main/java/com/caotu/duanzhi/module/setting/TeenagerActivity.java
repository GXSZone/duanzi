package com.caotu.duanzhi.module.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.PayPsdInputView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 青少年模式设置页面
 */
public class TeenagerActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_MODE = "MODE";
    public static final String KEY_PSD = "PSD";
    private TextView mTvIsTeenagerMode, mTvPsdMsg, mPsdTypeTitle;
    private ImageView mIvIsTeenagerMode;
    private PayPsdInputView mEtPsd;
    private RTextView mBtPsdSetup;
    private LinearLayout mLlStartAndCloseTeenager, mLlPsdSet;
    private boolean isTeenagerOpen;

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mEtPsd = findViewById(R.id.et_psd);
        //如果返回上一步可以设置 inputView.setComparePassword("");
        mEtPsd.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {
                if (hasSetPsd) {
                    ToastUtil.showShort("密码错误,请重新输入");
                } else {
                    ToastUtil.showShort("两次密码输入不同");
                }
                mEtPsd.postDelayed(() -> mEtPsd.cleanPsd(), 300);
                mBtPsdSetup.setEnabled(false);
            }

            @Override
            public void onEqual(String psd) {
                mBtPsdSetup.setEnabled(true);
            }

            @Override
            public void inputFinished(String inputPsd) {
                // 第一次输完逻辑  设置成功密码在点下一步里设置
                mBtPsdSetup.setEnabled(true);
            }

            @Override
            public void inputNotFinish() {
                mBtPsdSetup.setEnabled(false);
            }
        });
        mTvIsTeenagerMode = findViewById(R.id.tv_is_teenager_mode);
        mIvIsTeenagerMode = findViewById(R.id.iv_is_teenager_mode);

        mPsdTypeTitle = findViewById(R.id.psd_type_title);
        mTvPsdMsg = findViewById(R.id.tv_psd_msg);
        mBtPsdSetup = findViewById(R.id.bt_psd_setup);
        mBtPsdSetup.setOnClickListener(this);
        mIvIsTeenagerMode.setOnClickListener(this);
        getIntentDate();
    }

    boolean hasSetPsd = false;

    private void getIntentDate() {
        isTeenagerOpen = getIntent().getBooleanExtra(KEY_MODE, false);
        mLlStartAndCloseTeenager = findViewById(R.id.ll_start_and_close_teenager);
        mLlPsdSet = findViewById(R.id.ll_psd_set);
        if (isTeenagerOpen) {
            openMode();
        } else {
            closeMode();
        }
        //设置完成密码则就没有确认输入的操作了
        String extra = getIntent().getStringExtra(KEY_PSD);
        if (!TextUtils.isEmpty(extra)) {
            mEtPsd.setComparePassword(extra);
        }
        hasSetPsd = !TextUtils.isEmpty(extra); //如果传入有密码,说明已经设置密码
    }

    /**
     * 关闭状态,展示开启UI
     */
    private void closeMode() {
        mTvIsTeenagerMode.setText("青少年模式未开启");
        mIvIsTeenagerMode.setImageResource(R.mipmap.teenagermode_startbotton);
    }

    /***
     * 开启状态, 展示关闭UI
     */
    private void openMode() {
        mTvIsTeenagerMode.setText("青少年模式已开启");
        mIvIsTeenagerMode.setImageResource(R.mipmap.teenagermode_closebotton);
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_teenager;
    }

    /**
     * 这里需要判断当前再哪个界面,需要一层层返回
     */
    @Override
    public void onBackPressed() {
        if (type == 2) {
            //在设置密码的确认页面按返回
            mEtPsd.setComparePassword("");
            mEtPsd.cleanPsd();
            setPsdSetupOne();
        } else if (type == 1) {
            mEtPsd.setComparePassword("");
            mEtPsd.cleanPsd();
            mLlStartAndCloseTeenager.setVisibility(View.VISIBLE);
            mLlPsdSet.setVisibility(View.GONE);
            type = 0;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 该字段用来处理返回键问题
     */
    int type = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.bt_psd_setup:
                //如果留在设置密码这步则是下一步,否则是确定
                if (TextUtils.equals(mBtPsdSetup.getText().toString(), "下一步")) {
                    mEtPsd.setComparePassword(mEtPsd.getPasswordString());
                    mEtPsd.cleanPsd();

                    mPsdTypeTitle.setText("确认密码");
                    mTvPsdMsg.setVisibility(View.INVISIBLE);
                    mBtPsdSetup.setText("确定");
                    type = 2;
                } else {
                    psdIsRight();
                }
                break;
            case R.id.iv_is_teenager_mode:
                //关闭输入密码跳转
                if (isTeenagerOpen) {
                    closeTeenagerMode();
                    return;
                }
                //  判断是否设置了密码,设置了密码则直接开启,不然就是跳转到设置密码页面
                if (hasSetPsd) {
                    psdIsRight();
                } else {
                    mLlStartAndCloseTeenager.setVisibility(View.GONE);
                    mLlPsdSet.setVisibility(View.VISIBLE);
                    setPsdSetupOne();
                }
        }
    }

    private void closeTeenagerMode() {
        mLlStartAndCloseTeenager.setVisibility(View.GONE);
        mLlPsdSet.setVisibility(View.VISIBLE);
        type = 1;
        mPsdTypeTitle.setText("输入密码");
        mTvPsdMsg.setText("关闭青少年模式，需要输入密码");
        mBtPsdSetup.setText("确定");
        mBtPsdSetup.postDelayed(() -> {
            mEtPsd.requestFocus();
            showKeyboard(mEtPsd);
        }, 200);
    }

    private void psdIsRight() {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("youngmod", !isTeenagerOpen ? "1" : "0");
        params.put("youngpsd", mEtPsd.getPasswordString());
        OkGo.<BaseResponseBean<Object>>post(HttpApi.TEENAGER_MODE)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<Object>>() {

                    @Override
                    public void onSuccess(Response<BaseResponseBean<Object>> response) {
                        sendEventAndBack();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<Object>> response) {
                        ToastUtil.showShort("密码错误");
//                        super.onError(response);
                    }
                });
    }

    private void sendEventAndBack() {
        closeSoftKeyboard();
        CommonHttpRequest.getInstance().setTeenagerDateByUerInfo(!isTeenagerOpen, mEtPsd.getPasswordString());
        EventBusHelp.sendTeenagerEvent(!isTeenagerOpen);
        finish();
    }

    private void setPsdSetupOne() {
        mPsdTypeTitle.setText("设置密码");
        mTvPsdMsg.setVisibility(View.VISIBLE);
        mTvPsdMsg.setText("启动青少年模式，需要先设置独立密码");
        mBtPsdSetup.setText("下一步");
        type = 1;
        mBtPsdSetup.setEnabled(false);
        mBtPsdSetup.postDelayed(() -> {
            mEtPsd.requestFocus();
            showKeyboard(mEtPsd);
        }, 200);
    }
}
