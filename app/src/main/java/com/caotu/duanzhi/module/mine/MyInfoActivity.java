package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.UploadServiceTask;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.TextWatcherAdapter;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.LogUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvClickSave, mTvUserSex;
    private GlideImageView mIvChangeAvatar;
    private EditText mEtUserName;
    /**
     * 1995.8.12
     */
    private TextView mTvClickBirthday;
    private EditText mEtUserSign;
    private static InfoCallBack mCallback;
    String[] sexArray = new String[]{"男", "女"};
    //用户选择后的头像
    private String selectedPhoto;


    public static void openMyInfoActivity(UserBaseInfoBean.UserInfoBean userBean, InfoCallBack callBack) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        mCallback = callBack;
        Intent intent = new Intent(runningActivity,
                MyInfoActivity.class);
        intent.putExtra("userDate", userBean);
        runningActivity.startActivity(intent);
    }

    public interface InfoCallBack {
        void callback();
    }

    @Override
    protected void initView() {
        mTvClickSave = findViewById(R.id.tv_click_save);
        mIvChangeAvatar = findViewById(R.id.iv_change_avatar);
        mEtUserName = findViewById(R.id.et_user_name);
        mTvUserSex = findViewById(R.id.tv_user_sex);
        mTvClickBirthday = findViewById(R.id.tv_birthday);
        mEtUserSign = findViewById(R.id.et_user_sign);

        findViewById(R.id.iv_back).setOnClickListener(this);
        mTvClickSave.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                requestSave();
            }
        });
        mIvChangeAvatar.setOnClickListener(this);
        findViewById(R.id.rl_click_change_sex).setOnClickListener(this);
        mTvClickBirthday.setOnClickListener(this);
        findViewById(R.id.rl_click_birthday).setOnClickListener(this);
        getDateAndBind();
        initEditListener();
    }

    private void initEditListener() {
        mEtUserName.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameStr = s.toString().trim();
            }
        });

        mEtUserSign.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signStr = s.toString().trim();
            }
        });
    }

    private void getDateAndBind() {
        UserBaseInfoBean.UserInfoBean userBean = getIntent().getParcelableExtra("userDate");
        if (userBean == null) {
            return;
        }
        String userbirthday = userBean.getUserbirthday();
        if (TextUtils.isEmpty(userbirthday)) {
            Calendar calendar = Calendar.getInstance();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            initBirthDay(mYear, mMonth, mDay);
        }
        mTvClickBirthday.setText(userbirthday);
        mIvChangeAvatar
                .loadCircle(userBean.getUserheadphoto(), R.mipmap.ic_launcher);
        mEtUserName.setText(userBean.getUsername());
        mEtUserName.setSelection(userBean.getUsername().length());
        mEtUserSign.setText(userBean.getUsersign());
        String usersex = userBean.getUsersex();
        if ("0".equals(usersex)) {
            sexStr = 0;
        } else if ("1".equals(usersex)) {
            sexStr = 1;
        } else {
            sexStr = -1;
        }
        mTvUserSex.setText(sexStr == -1 ? "" : sexArray[sexStr]);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_my_info;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_change_avatar:
                changeAvatar();
                break;
            case R.id.rl_click_change_sex:
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(sexArray, sexStr, (DialogInterface dialog1, int which) -> {
                            mTvUserSex.setText(sexArray[which]);
                            sexStr = which;
                            dialog1.dismiss();
                        })
                        .show();
                break;
            case R.id.rl_click_birthday:
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                    initBirthDay(mYear, mMonth, mDay);
                }, 1990, 10, 2);
                dialog.show();
                break;
        }
    }

    private void initBirthDay(int year, int month, int dayOfMonth) {
        StringBuilder builder = new StringBuilder();
        builder.append(year).append(".").append(month).append(".").append(dayOfMonth);
        mTvClickBirthday.setText(builder.toString());
    }


    private void changeAvatar() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//图片，视频，音频，全部
                .theme(R.style.picture_QQ_style)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)//是否可预览图片 true or false
                //.compressGrade(Luban.THIRD_GEAR)
                .isZoomAnim(true)
                .compress(true)
                .imageSpanCount(3)
                .isCamera(true)
                .enableCrop(true)
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.REQUEST_PICTURE);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == PictureConfig.REQUEST_PICTURE) {
            // 图片、视频、音频选择结果回调
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            LocalMedia localMedia = selectList.get(0);
            if (localMedia.isCompressed()) {
                selectedPhoto = localMedia.getCompressPath();
            } else {
                selectedPhoto = localMedia.getPath();
            }
            mIvChangeAvatar.loadCircle(selectedPhoto);
        }
    }

    private String internetUrl, nameStr, signStr;
    private int sexStr = -1;

    private void requestSave() {
        if (!TextUtils.isEmpty(selectedPhoto)) {
            uploadUserAvatar();
        } else {
            requestSetUserInfo();
        }
    }

    private void uploadUserAvatar() {
        UploadServiceTask.upLoadFile(".jpg", selectedPhoto, new UploadServiceTask.OnUpLoadListener() {
            @Override
            public void onUpLoad(long progress, long max) {
                float result = (float) (progress * 100.0 / max);
                LogUtil.logString("progress =" + (long) result + "%");
            }

            @Override
            public void onLoadSuccess(String url) {
                internetUrl = "https://" + url;
                requestSetUserInfo();
            }

            @Override
            public void onLoadError(String exception) {
                ToastUtil.showShort("上传失败");
            }
        });
    }

    private void requestSetUserInfo() {
        Map<String, String> map = new HashMap<>();
        String birthday = mTvClickBirthday.getText().toString();
        if (!TextUtils.isEmpty(birthday)) {
            map.put("userbirthday", birthday);
        }
        if (!TextUtils.isEmpty(internetUrl)) {
            map.put("userheadphoto", internetUrl);
        }
        if (!TextUtils.isEmpty(nameStr)) {
            map.put("username", nameStr);
        }
        if (sexStr != -1) {
            map.put("usersex", String.valueOf(sexStr));
        }
        if (!TextUtils.isEmpty(signStr)) {
            map.put("usersign", signStr);
        }
        if (map.size() == 0) {
            finish();
            return;
        }
        OkGo.<BaseResponseBean<String>>post(HttpApi.SET_USER_BASE_INFO)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("保存成功！");
                        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                        PictureFileUtils.deleteCacheDirFile(MyApplication.getInstance());
                        if (mCallback != null) {
                            mCallback.callback();
                        }
                        finish();
                        mCallback = null;
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        String message = response.getException().getMessage();
                        if (HttpCode.user_has_exsit.equals(message)) {
                            ToastUtil.showShort("该用户已存在");
                        } else if (HttpCode.user_name.equals(message)) {
                            ToastUtil.showShort("用户昵称存在敏感词,改一下呗");
                        } else if (HttpCode.user_sign.equals(message)) {
                            ToastUtil.showShort("用户签名存在敏感词,改一下呗");
                        } else if (HttpCode.cannot_change_user_name.equals(message)) {
                            ToastUtil.showShort("昵称一个月只能修改一次哦~");
                        }
                        ToastUtil.showShort(response.body().getMessage());
                        super.onError(response);
                    }
                });
    }


}
