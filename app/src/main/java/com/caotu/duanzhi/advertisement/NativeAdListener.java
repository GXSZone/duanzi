package com.caotu.duanzhi.advertisement;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;

import com.caotu.duanzhi.other.UmengHelper;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一处理埋点事件
 */
public class NativeAdListener implements NativeExpressAD.NativeExpressADListener {
    /**
     * 0=列表信息流  1= banner  2=详情头布局广告  3=评论列表
     */
    int keyType;
    NativeExpressADView nativeExpressADView;
    List<NativeExpressADView> adList = new ArrayList<>(32);
    private String TAG = "NativeAdListener";

    public NativeAdListener(int type) {
        keyType = type;
    }

    /**
     * 针对列表要获取多个广告view的时候
     *
     * @return
     */
    public List<NativeExpressADView> getAdList() {
        return adList;
    }

    /**
     * 针对只有一个广告view的时候
     *
     * @return
     */
    public NativeExpressADView getNativeExpressADView() {
        return nativeExpressADView;
    }

    @CallSuper
    @Override
    public void onADLoaded(List<NativeExpressADView> list) {
        Log.i(TAG, "onADLoaded: ");
        if (list != null && list.size() > 0) {
            nativeExpressADView = list.get(0);
            adList.addAll(list);
        }
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onRenderFail: ");
    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
        Log.i(TAG, "onRenderSuccess: ");
    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {
        if (keyType == 0) {
            UmengHelper.event(ADConfig.item_show);
        } else if (keyType == 1) {
            UmengHelper.event(ADConfig.banner_show);
        } else if (keyType == 2) {
            UmengHelper.event(ADConfig.detail_header_show);
        } else if (keyType == 3) {
            UmengHelper.event(ADConfig.comment_show);
        }
        Log.i(TAG, "onADExposure: 广告曝光"+nativeExpressADView.getMeasuredHeight());
    }

    /**
     * 埋点的点击事件统一处理
     *
     * @param nativeExpressADView
     */
    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {
        String eventKey = "";
        if (keyType == 0) {
            eventKey = ADConfig.item_click;
        } else if (keyType == 1) {
            eventKey = ADConfig.banner_click;
        } else if (keyType == 2) {
            eventKey = ADConfig.detail_header_click;
        } else if (keyType == 3) {
            eventKey = ADConfig.comment_click;
        }
        if (TextUtils.isEmpty(eventKey)) return;
        UmengHelper.event(eventKey);
    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {
        if (nativeExpressADView != null) {
            if (keyType == 2) {
                ViewGroup parent = (ViewGroup) nativeExpressADView.getParent();
                if (parent != null) {
                    ViewGroup parent1 = (ViewGroup) parent.getParent();
                    //详情头正常是一个ll,因为有分割线,所以需要单独处理一下
                    if (parent1 != null) {
                        parent1.setVisibility(View.GONE);
                    }
                    parent.removeView(nativeExpressADView);
                }
            }
            nativeExpressADView.destroy();
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(TAG, "onNoAD: "+adError.getErrorMsg());
    }
}
