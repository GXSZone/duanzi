package com.caotu.duanzhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.caotu.duanzhi.advertisement.ADConfig;
import com.caotu.duanzhi.advertisement.ADUtils;
import com.caotu.duanzhi.advertisement.NativeAdListener;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;

/**
 * 指纹识别 代码参考:https://guolin.blog.csdn.net/article/details/81450114
 */
public class TestActivity extends AppCompatActivity {

    private FrameLayout mFlAdContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }

    private void initView() {

        mFlAdContainer = (FrameLayout) findViewById(R.id.fl_ad_container);
        ADUtils.getNativeAd(this, ADConfig.detail_id, 1, new NativeAdListener(1) {
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                super.onADLoaded(list);
                List<NativeExpressADView> adList = getAdList();
                NativeExpressADView nativeExpressADView = adList.get(0);
                nativeExpressADView.render();
                mFlAdContainer.addView(nativeExpressADView);
            }
        });
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

    public void getAdInfo(View view) {
        View childAt = mFlAdContainer.getChildAt(0);
        Log.i("mFlAdContainer", "getAdInfo: "+childAt.getLayoutParams());
    }
}
