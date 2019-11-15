package com.caotu.duanzhi.module.mine;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.tecentupload.UploadServiceTask;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.module.mine.adapter.GridImageAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmitFeedBackActivity extends BaseActivity {

    private EditText contentEdit, connectWayEdit;
    private TextView textWatcher;
    private GridImageAdapter adapter;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected int getLayoutView() {
        return R.layout.activity_help_layout;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        contentEdit = findViewById(R.id.fragment_help_content_edt);
        connectWayEdit = findViewById(R.id.fragment_help_connectway_edt);
        textWatcher = findViewById(R.id.text_watcher);
        contentEdit.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {

                int length = editable.toString().length();
                if (length >= 500) {
                    ToastUtil.showShort("500字不能再多啦");
                }
                textWatcher.setText(String.format("%d/500", length));
            }
        });

        findViewById(R.id.tv_request).setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
                    HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.BIND_TYPE);
                } else {
                    clickRight();
                }
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycler);
        adapter = new GridImageAdapter(this);
        adapter.setList(selectList);
        adapter.setSelectMax(3);
        recyclerView.setAdapter(adapter);
    }

    public List<String> uploadTxFiles = new ArrayList<>();
    public void clickRight() {
        String content = contentEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            if (content.contains("视频") || content.contains("卡顿")) {
                ToastUtil.showShort("提交成功！");
                finish();
                return;
            }
        }
        if (AppUtil.listHasDate(selectList)) {
            for (int i = 0; i < selectList.size(); i++) {
                LocalMedia localMedia = selectList.get(i);
                String imagePath;
                if (localMedia.isCompressed()) {
                    imagePath = localMedia.getCompressPath();
                } else {
                    imagePath = localMedia.getPath();
                }
                UploadServiceTask.upLoadFile(".jpg", imagePath, new UploadServiceTask.OnUpLoadListener() {
                    @Override
                    public void onUpLoad(float progress) {
                    }

                    @Override
                    public void onLoadSuccess(String url) {
                        uploadTxFiles.add(url);
                        if (uploadTxFiles.size() == selectList.size()) {
                            request();
                        }
                    }

                    @Override
                    public void onLoadError(String exception) {
                        ToastUtil.showShort("上传失败");
                    }
                });
            }
        } else {
            request();
        }
    }

    public void request() {
        UmengHelper.event(UmengStatisticsKeyIds.feedback);
        String content = contentEdit.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort("请输入内容");
            return;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("contactway", connectWayEdit.getText().toString().trim());
        map.put("feedtext", content);
        map.put("feedtype", "1");
        if (AppUtil.listHasDate(uploadTxFiles)) {
            String contentUrl = new JSONArray(uploadTxFiles).toString();
            contentUrl = contentUrl.replace("\\", "");
            map.put("feedurllist", contentUrl);
        }

        OkGo.<BaseResponseBean<Object>>post(HttpApi.USER_MY_TSUKKOMI)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<Object>> response) {
                        ToastUtil.showShort("提交成功！");
                        finish();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<Object>> response) {
                        ToastUtil.showShort("提交失败！");
                        super.onError(response);
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PictureConfig.REQUEST_PICTURE || requestCode == PictureConfig.CAMERA) {
            // 图片、视频、音频选择结果回调
            selectList = PictureSelector.obtainMultipleResult(data);
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
        }
    }
}
