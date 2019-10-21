package com.caotu.duanzhi.advertisement;

import com.caotu.duanzhi.other.UmengHelper;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.List;

/**
 * 统一处理埋点事件
 */
public class NativeAdListener implements NativeExpressAD.NativeExpressADListener {
    /**
     * 0=列表信息流  1= banner
     */
    int keyType;
    NativeExpressADView nativeExpressADView;

    public NativeAdListener(int type) {
        keyType = type;
    }

    public NativeExpressADView getNativeExpressADView() {
        return nativeExpressADView;
    }

    @Override
    public void onADLoaded(List<NativeExpressADView> list) {
        if (list != null && list.size() > 0) {
            nativeExpressADView = list.get(0);
            if (keyType == 1) {
                UmengHelper.event(ADConfig.banner_show);
            } else if (keyType == 0) {
                UmengHelper.event(ADConfig.item_show);
            }
        }
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {

    }

    /**
     * 埋点的点击事件统一处理
     *
     * @param nativeExpressADView
     */
    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {
        String eventKey="";
        if (keyType == 0) {
            eventKey = ADConfig.item_click;
        } else if (keyType == 1) {
            eventKey = ADConfig.banner_click;
        }
        UmengHelper.event(eventKey);
    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {

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

    }
}
