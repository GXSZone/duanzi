package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.StretchConsumer;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.tecentupload.UploadServiceTask;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.luck.picture.lib.PictureSelectionModel;
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

/**
 * 2019-06-17 默认地址显示 需要注意北京,上海,天津三个市级
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvUserSex;
    private GlideImageView mIvChangeAvatar;
    private EditText mEtUserName;
    /**
     * 1995.8.12
     */
    private TextView mTvClickBirthday, mTvLocation;
    private EditText mEtUserSign;

    String[] sexArray = new String[]{"男", "女"};
    //用户选择后的头像
    private String selectedPhoto;
    private CityPickerView mPicker;
    private String[] location;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //申明对象
        mPicker = new CityPickerView();
        //预先加载仿iOS滚轮实现的全部数据
        mPicker.init(this);
        String initLocation = MySpUtils.getString(MySpUtils.SP_MY_LOCATION);

        //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
        CityConfig.Builder builder = new CityConfig.Builder()
                .setCityWheelType(CityConfig.WheelType.PRO_CITY)
                .setShowGAT(true);

        if (!TextUtils.isEmpty(initLocation) && initLocation.contains(",")) {
            String[] split = initLocation.split(",");
            if (TextUtils.equals(split[0], split[1]) || split[0].contains("市")) {
                builder.province(split[0]);
                mTvLocation.setText(split[0]);
            } else {
                builder.province(split[0])//默认显示的省份
                        .city(split[1]);
                mTvLocation.setText(String.format("%s,%s", split[0], split[1]));
            }
        }
        mPicker.setConfig(builder.build());

    }

    public static void openMyInfoActivity(UserBaseInfoBean.UserInfoBean userBean) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity, MyInfoActivity.class);
        intent.putExtra("userDate", userBean);
        runningActivity.startActivity(intent);
    }

    @Override
    protected void initView() {
        View scroll = findViewById(R.id.scroll_layout);
        SmartSwipe.wrap(scroll)
                .addConsumer(new StretchConsumer())
                .enableVertical(); //工作方向：纵向
        findViewById(R.id.tv_click_save).setOnClickListener(this);
        mIvChangeAvatar = findViewById(R.id.iv_change_avatar);
        mEtUserName = findViewById(R.id.et_user_name);
        mTvUserSex = findViewById(R.id.tv_user_sex);
        mTvClickBirthday = findViewById(R.id.tv_birthday);
        mEtUserSign = findViewById(R.id.et_user_sign);

        findViewById(R.id.iv_back).setOnClickListener(this);

        findViewById(R.id.rl_click_change_sex).setOnClickListener(this);
        findViewById(R.id.rl_click_birthday).setOnClickListener(this);
        findViewById(R.id.rl_change_avatar).setOnClickListener(this);
        mTvLocation = findViewById(R.id.tv_user_location);
        mTvLocation.setOnClickListener(this);
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
                .loadCircle(userBean.getUserheadphoto(), R.mipmap.touxiang_moren);
        mEtUserName.setText(userBean.getUsername());
        if (!TextUtils.isEmpty(mEtUserName.getText().toString())) {
            mEtUserName.setSelection(mEtUserName.getText().toString().length());
        }
        initName = userBean.getUsername();
        nameStr = initName;  //用户如果啥都没改动则edittext监听是没有的
        mEtUserSign.setText(userBean.getUsersign());
        signStr = userBean.getUsersign();

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
            case R.id.tv_click_save:
                requestSave();
                break;
            case R.id.rl_change_avatar:
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
                dealBirthDay();
                break;
            case R.id.tv_user_location:
                //监听选择点击事件及返回结果
                mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    @Override
                    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                        String name = province.getName(); //省份province
                        String cityName = city.getName();  //城市city
                        location = new String[2];
                        location[0] = name;
                        location[1] = cityName;
                        mTvLocation.setText(getLocationText(true));
                    }
                });
                //显示
                mPicker.showCityPicker();
                break;
        }
    }

    public String getLocationText(boolean isJustShow) {
        if (location == null) return "";
        if (location[0].endsWith("市") && isJustShow) {
            return location[0];
        }
        return location[0] + "," + location[1];
    }

    private void dealBirthDay() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        try {
            String userbirthday = mTvClickBirthday.getText().toString();
            if (!TextUtils.isEmpty(userbirthday) && userbirthday.contains(".")) {
                String[] split = userbirthday.split("\\.");
                if (split.length == 3) {
                    mYear = Integer.parseInt(split[0]);
                    mMonth = Integer.parseInt(split[1]) - 1;
                    mDay = Integer.parseInt(split[2]);
                }
            } else if (!TextUtils.isEmpty(userbirthday) && !userbirthday.contains(".")) {
                mYear = Integer.parseInt(userbirthday.substring(0, 4));
                mMonth = Integer.parseInt(userbirthday.substring(4, 6)) - 1;
                mDay = Integer.parseInt(userbirthday.substring(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            initBirthDay(year, month, dayOfMonth);
        }, mYear, mMonth, mDay);
        dialog.show();
    }

    private void initBirthDay(int year, int month, int dayOfMonth) {
        StringBuilder builder = new StringBuilder();
        builder.append(year).append(".").append(month + 1).append(".").append(dayOfMonth);
        mTvClickBirthday.setText(builder.toString());
    }


    private void changeAvatar() {
        PictureSelectionModel model = PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage());
        if (DevicesUtils.isOppo()) {
            model.theme(R.style.picture_default_style);
        } else {
            model.theme(R.style.picture_QQ_style);
        }
        model//图片，视频，音频，全部
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
    //初始化name
    private String initName;
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
            public void onUpLoad(float progress) {

            }

            @Override
            public void onLoadSuccess(String url) {
                internetUrl = url;
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
        //不相等的才传接口
        if (!TextUtils.equals(nameStr, initName)) {
            map.put("username", nameStr);
        }
        if (sexStr != -1) {
            map.put("usersex", String.valueOf(sexStr));
        }
        map.put("usersign", signStr);
        if (location != null) {
            map.put("location", getLocationText(false));
            MySpUtils.putString(MySpUtils.SP_MY_LOCATION, getLocationText(false));
        }

        OkGo.<BaseResponseBean<String>>post(HttpApi.SET_USER_BASE_INFO)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("保存成功！");
                        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                        PictureFileUtils.deleteCacheDirFile(MyApplication.getInstance());
                        finish();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        String message = response.getException().getMessage();
                        if (HttpCode.user_has_exsit.equals(message)) {
                            ToastUtil.showShort("该用户已存在");
                        } else if (HttpCode.user_name.equals(message) || HttpCode.user_sign.equals(message)) {
                            ToastUtil.showShort("昵称重复啦，换一个试试");
                        } else if (HttpCode.cannot_change_user_name.equals(message)) {
                            ToastUtil.showShort("昵称一个月只能修改一次哦~");
                        } else {
                            super.onError(response);
                        }
                    }
                });
    }

}
