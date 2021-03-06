package com.caotu.duanzhi.view.dialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail.ContentItemAdapter;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.IViewDetail;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.EditTextLib.SpXEditText;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ReplyDialog extends Dialog implements View.OnClickListener {

    private RecyclerView mRvSelect;
    private SpXEditText mEtSendContent;
    private ImageView mIvQuickReply;
    private TextView mTvClickSend;
    private ListView mRvQuick;
    //内容选择字段
    private List<LocalMedia> selectList;
    private int publishType;
    private IViewDetail mCallBack;
    boolean isBottomShow;
    CharSequence mHintText;
    int keyboardHeight;

    public ReplyDialog(@NonNull Context context, @NonNull IViewDetail callback) {
        super(context, R.style.customDialog);
        mCallBack = callback;
        keyboardHeight = DevicesUtils.dp2px(250);
        Window window = getWindow();
        if (window == null) return;
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setDimAmount(0f);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    private boolean isSoftShowing() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvQuick.getLayoutParams();
        if (params.height > 0) {
            return false;
        }
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        int device = DevicesUtils.getScreenHeight();
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight < device * 3 / 4;
    }

    /**
     * 初始化之后调用,用于设置一些初始化显示相关的参数
     *
     * @param isShowListStr
     * @param hintText
     */
    public void setParams(boolean isShowListStr, CharSequence hintText) {
        isBottomShow = isShowListStr;
        mHintText = hintText;
    }

    public void showKeyboard() {
        if (mEtSendContent == null) return;
        mEtSendContent.requestFocus();
        mEtSendContent.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) mEtSendContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEtSendContent, InputMethodManager.SHOW_FORCED);
    }

    public void closeSoftKeyboard() {
        if (mEtSendContent == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) mEtSendContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEtSendContent.getWindowToken(), 0);
    }

    protected void initView() {
        mRvSelect = findViewById(R.id.rv_select);
        mEtSendContent = findViewById(R.id.et_send_content);
        mIvQuickReply = findViewById(R.id.iv_quick_reply);
        mTvClickSend = findViewById(R.id.tv_click_send);
        mRvQuick = findViewById(R.id.rv_quick);

        findViewById(R.id.iv_detail_video1).setOnClickListener(this);
        findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        findViewById(R.id.iv_detail_at).setOnClickListener(this);
        mIvQuickReply.setOnClickListener(this);
        mTvClickSend.setOnClickListener(this);

        mEtSendContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mTvClickSend.setEnabled(true);
                } else if (TextUtils.isEmpty(s) && !AppUtil.listHasDate(selectList)) {
                    mTvClickSend.setEnabled(false);
                }
            }
        });
        initQuickReply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_detail_photo1:
                if (AppUtil.listHasDate(selectList) && publishType == 2) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage("若你要添加图片，已选视频将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog13, which) -> {
                                dialog13.dismiss();
                                selectList.clear();
                                mRvSelect.setVisibility(View.GONE);
                                mCallBack.getPresenter().getPicture();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog14, which) -> dialog14.dismiss()).create();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                } else {
                    mCallBack.getPresenter().getPicture();
                }

                break;
            case R.id.iv_detail_video1:
                if (AppUtil.listHasDate(selectList) && publishType == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).setMessage("若你要添加视频，已选图片将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog12, which) -> {
                                dialog12.dismiss();
                                selectList.clear();
                                mRvSelect.setVisibility(View.GONE);
                                mCallBack.getPresenter().getVideo();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                            .create();

                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                } else {
                    mCallBack.getPresenter().getVideo();
                }

                break;
            case R.id.tv_click_send:
                UmengHelper.event(UmengStatisticsKeyIds.comment_publish);
                //为了防止已经在发布内容视频,再在评论里发布视频处理不过来
                Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
                if (lastSecondActivity instanceof MainActivity) {
                    boolean publishing = ((MainActivity) lastSecondActivity).isPublishing();
                    if (publishing) {
                        ToastUtil.showShort("正在发布内容中,请稍后再试");
                        return;
                    }
                }

                if (LoginHelp.isLoginAndSkipLogin()) {
                    mCallBack.getPresenter().publishBtClick();
                }
                break;
            case R.id.iv_detail_at:
                UmengHelper.event(UmengStatisticsKeyIds.comments_at);
                HelperForStartActivity.openSearch();
                break;
            case R.id.iv_quick_reply:
                changeKeyboardAndReplyView();
                break;
        }
    }

    private void initQuickReply() {
        mEtSendContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvQuick.getLayoutParams();
                    if (params.height > 0) {
                        params.height = 0;
                        mRvQuick.setLayoutParams(params);
                    }
                }
                return false;
            }
        });

        if (!AppUtil.listHasDate(CommonHttpRequest.hotComments)) {
            mIvQuickReply.setVisibility(View.GONE);
            return;
        }
        bindRvDate();
    }

    private ContentItemAdapter dateAdapter;

    private void showRV() {
        mTvClickSend.setEnabled(true);
        mRvSelect.setVisibility(View.VISIBLE);

        if (dateAdapter == null) {
            dateAdapter = new ContentItemAdapter();
            dateAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                adapter.remove(position);
                if (adapter.getData().size() == 0) {
                    mRvSelect.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(mEtSendContent.getText())) {
                        mTvClickSend.setEnabled(false);
                    }
                }
                mCallBack.getPresenter().setMediaList(adapter.getData());
            });
            mRvSelect.setAdapter(dateAdapter);
        }
        dateAdapter.setNewData(selectList);
    }

    public void bindRvDate() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.item_reply_layout, CommonHttpRequest.hotComments);
        mRvQuick.setAdapter(adapter);
        mRvQuick.setOnItemClickListener((parent, view, position, id) -> {
            String s = CommonHttpRequest.hotComments.get(position);
            UmengHelper.event("xzrc");
            int selectionStart = mEtSendContent.getSelectionStart();
            if (mEtSendContent.getText() != null) {
                mEtSendContent.getText().insert(selectionStart < 0 ? 0 : selectionStart, s.concat(" "));
            }
        });
    }

    public void changeKeyboardAndReplyView() {
        UmengHelper.event("rcan");
        if (isSoftShowing()) {
            ValueAnimator animator = ValueAnimator.ofInt(0, keyboardHeight);
            animator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvQuick.getLayoutParams();
                params.height = animatedValue;
                mRvQuick.setLayoutParams(params);
            });
            animator.setDuration(50);
            animator.start();
            closeSoftKeyboard();
            mIvQuickReply.setImageResource(R.drawable.keyboard_button_big_normal);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvQuick.getLayoutParams();
            params.height = 0;
            mRvQuick.setLayoutParams(params);
            showKeyboard();
            mIvQuickReply.setImageResource(R.drawable.ao_button);
        }
    }

    /**
     * 供外部fragment 调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 2;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    mCallBack.getPresenter().setMediaList(selectList);
                    mCallBack.getPresenter().setIsVideo(true);
                    showRV();
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    mCallBack.getPresenter().setMediaList(selectList);
                    mCallBack.getPresenter().setIsVideo(false);
                    showRV();
                    break;
                case HelperForStartActivity.at_user_requestCode:
                    UserBean extra = data.getParcelableExtra(HelperForStartActivity.KEY_AT_USER);
                    if (extra != null && mEtSendContent != null) {
                        mEtSendContent.addSpan(extra);
                    }
                    break;
            }
        }
    }


    public EditText getEditView() {
        return mEtSendContent;
    }


    public View getPublishView() {
        return mTvClickSend;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detail_reply);
        initView();
    }

    @Override
    public void show() {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (activity.isFinishing() || activity.isDestroyed()) return;
        }
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mEtSendContent == null) return;
        if (!TextUtils.isEmpty(mHintText)) {
            mEtSendContent.setHint(mHintText);
        }
        if (!isBottomShow) {
            mIvQuickReply.setImageResource(R.drawable.ao_button);
            mEtSendContent.postDelayed(this::showKeyboard, 100);
        } else if (mRvQuick != null) {
            mIvQuickReply.setImageResource(R.drawable.keyboard_button_big_normal);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvQuick.getLayoutParams();
            params.height = keyboardHeight;
            mRvQuick.setLayoutParams(params);
            closeSoftKeyboard();
        }
        if (!AppUtil.listHasDate(selectList)) {
            mRvSelect.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismiss() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRvQuick.getLayoutParams();
        if (params.height > 0) {
            params.height = 0;
            mRvQuick.setLayoutParams(params);
        }
        closeSoftKeyboard();
        super.dismiss();
    }

    public void dismissByClearDate() {
        if (AppUtil.listHasDate(selectList)) {
            selectList.clear();
        }
        if (mRvSelect != null) {
            mRvSelect.setVisibility(View.GONE);
        }
        if (mEtSendContent != null && mEtSendContent.getText() != null) {
            mEtSendContent.getText().clear();
        }
        dismiss();
    }
}
