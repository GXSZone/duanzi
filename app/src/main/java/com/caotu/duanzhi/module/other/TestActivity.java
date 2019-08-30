package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.TimerView;
import com.caotu.duanzhi.view.widget.WeiboEditText.AtTextWatcher;
import com.caotu.duanzhi.view.widget.WeiboEditText.CopyWeChatEditText;
import com.caotu.duanzhi.view.widget.WeiboEditText.WeiboEdittext;

import java.io.File;
import java.util.Properties;

/**
 * 指纹识别 代码参考:https://guolin.blog.csdn.net/article/details/81450114
 */
public class TestActivity extends AppCompatActivity {


    private String VIDEOPATH;
    private TextView mVideoPath;
    private CopyWeChatEditText mCopyWeChat;
    private WeiboEdittext weiboText;
    private RadioGroup mRadioGroup;
    private TimerView timerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }

    public void change(View view) {
        String absoluteVideoByWaterPath = PathConfig.getAbsoluteVideoByWaterPath(0);
        String absoluteVideoByWaterPath1 = PathConfig.getAbsoluteVideoByWaterPath(1);
        File file = new File(absoluteVideoByWaterPath);
        File file1 = new File(absoluteVideoByWaterPath1);
        ToastUtil.showShort("file1Water:" + file.exists() + "------file2water:" + file1.exists());
        /**
         * 从相册中选择视频
         */

//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, 66);


    }


    private void initView() {
        mCopyWeChat = findViewById(R.id.copy_wechat);
        //一定要在这里面设置监听，否则删除会出现问题。如果有更好的办法请告知我，谢谢


        weiboText = findViewById(R.id.weibo_edittext);

        weiboText.addTextChangedListener(new AtTextWatcher() {
            @Override
            public void ByDealAt() {
                HelperForStartActivity.openSearch();
                ToastUtil.showShort("触发@功能");
            }
        });

//        mImageChange = (ImageView) findViewById(R.id.image_change);
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i("RadioGroup", "onCheckedChanged: " + checkedId);
            }
        });
        timerview = findViewById(R.id.timer_skip);

//        LottieAnimationView view = findViewById(R.id.animation_view);
//        view.setImageAssetsFolder("images");
//        view.setAnimation("data.json");
//
//        view.playAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_CANCELED) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void bt_add(View view) {
        //注意添加需要自己拼接@ 符号
//        mCopyWeChat.addSpan(extra, "@啦啦啦 ");

//        RObject object = new RObject();
//        int id = (int) (Math.random() * 100);
//        object.setObjectText("双" + id + "狂欢");// 必须设置
//        weiboText.setObject(object);
    }

    boolean isBlack = false;

//    public void play(View view) {
//        VoiceUtils.playVoice(this);
////        test();
//        RvTestDialog dialog = new RvTestDialog();
//        dialog.show(getSupportFragmentManager(), "rvtest");
//
//    }

    /**
     * link {https://img-blog.csdn.net/20171222234017144?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2VpeGluXzM3MTM5MTk3/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast}
     * 获取指定键指示的系统属性
     */
    public void test() {
        //获取所有的属性
        Properties properties = System.getProperties();
        //遍历所有的属性
        for (String key : properties.stringPropertyNames()) {
            //输出对应的键和值
            Log.i("@@@@", key + "=" + properties.getProperty(key));
        }
    }

    //重写字体缩放比例 api<25
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            Configuration config = res.getConfiguration();
            config.fontScale = 0.5f;//1 设置正常字体大小的倍数
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }

    //重写字体缩放比例  api>25
    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            final Resources res = newBase.getResources();
            final Configuration config = res.getConfiguration();
            config.fontScale = 0.5f;//1 设置正常字体大小的倍数
            final Context newContext = newBase.createConfigurationContext(config);
            super.attachBaseContext(newContext);
        } else {
            super.attachBaseContext(newBase);
        }
    }

    public void openQQ(View view) {
        //倒计时开始
        timerview.setNormalText("跳过")
                .setCountDownText("跳过 ", "S")
                .setOnCountDownListener(new TimerView.OnCountDownListener() {
                    @Override
                    public void onClick() {
                        ToastUtil.showShort("点击事件");
                    }

                    @Override
                    public void onFinish() {
                        ToastUtil.showShort("倒计时结束");
                    }
                })
                .startCountDown(3);
//        joinQQGroup("KEiwphH1Tm0CGKw3EaoixZUe1rqJa9Ro");
    }

    /****************
     * 可参考文章: https://blog.csdn.net/weixin_33785972/article/details/88028475
     * 发起添加群流程。群号：内含段友app内测群(340362540) 的 key 为： KEiwphH1Tm0CGKw3EaoixZUe1rqJa9Ro
     * 调用 joinQQGroup(KEiwphH1Tm0CGKw3EaoixZUe1rqJa9Ro) 即可发起手Q客户端申请加群 内含段友app内测群(340362540)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
