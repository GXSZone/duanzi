package com.caotu.duanzhi.module.other;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.WeiboEditText.AtTextWatcher;
import com.caotu.duanzhi.view.widget.WeiboEditText.CopyWeChatEditText;
import com.caotu.duanzhi.view.widget.WeiboEditText.RObject;
import com.caotu.duanzhi.view.widget.WeiboEditText.WeiboEdittext;
import com.luck.picture.lib.tools.VoiceUtils;

import java.io.File;

import cn.jzvd.Jzvd;

/**
 * 指纹识别 代码参考:https://guolin.blog.csdn.net/article/details/81450114
 */
public class TestActivity extends AppCompatActivity {


    private String VIDEOPATH;
    private TextView mVideoPath;
    private CopyWeChatEditText mCopyWeChat;
    private WeiboEdittext weiboText;

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

        mCopyWeChat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return CopyWeChatEditText.KeyDownHelper(mCopyWeChat.getText());
                }
                return false;
            }
        });
        mCopyWeChat.addTextChangedListener(new AtTextWatcher() {
            @Override
            public void ByDealAt() {
                ToastUtil.showShort("触发@功能");
            }
        });
        weiboText = findViewById(R.id.weibo_edittext);
    }

    public void anim(View view) {
        if (mVideoPath != null) {
            LikeAndUnlikeUtil.showNoticeTip(mVideoPath);
        }
    }

    @Override
    protected void onDestroy() {
        Jzvd.releaseAllVideos();
        super.onDestroy();
    }

    public void bt_add(View view) {
        //注意添加需要自己拼接@ 符号
        mCopyWeChat.addSpan("@啦啦啦 ");

        RObject object = new RObject();
        int id = (int) (Math.random() * 100);
        object.setObjectText("双" + id + "狂欢");// 必须设置
        weiboText.setObject(object);
    }

    public void play(View view) {
        VoiceUtils.playVoice(this);
    }
}
