package com.caotu.adlib;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.admob.admobgensdk.ad.constant.InformationAdType;
import cn.admob.admobgensdk.ad.information.ADMobGenInformation;
import cn.admob.admobgensdk.ad.information.IADMobGenInformation;
import cn.admob.admobgensdk.ad.listener.SimpleADMobGenInformationAdListener;
import cn.admob.admobgensdk.ad.splash.ADMobGenSplashView;
import cn.admob.admobgensdk.common.ADMobGenSDK;
import cn.admob.admobgensdk.entity.ADMobGenSdkConfig;

/**
 * 这里还需要注意一个问题,广告的开关问题
 */
public class AdHelper implements AdDateRequest {
    private static final String TAG = "AdHelper";

    private static final AdHelper ourInstance = new AdHelper();
    BuriedPointListener mListener;

    public static AdHelper getInstance() {
        return ourInstance;
    }

    private AdHelper() {
    }

    public BuriedPointListener getBuriedPointListener() {
        return mListener;
    }


    //首页信息流广告都从这个类获取,并且记录
    List<IADMobGenInformation> adViewList;
    int count = 0;

    /**
     * 初始化广告
     *
     * @param context
     */
    public void initSDK(Context context, BuriedPointListener listener) {
        //埋点
        ADMobGenSDK.instance().initSdk(context, new ADMobGenSdkConfig.Builder()
                // 修改为自己的appId
                .appId(ADLibConfig.AdMobile_APPID)
                // PLATFORMS只需要选择所需的广告平台(其中PLAFORM_ADMOB是必须的)
                .platforms(ADLibConfig.PLATFORMS)
                // 如果APP在其他进程使用了WebView（没有可不设置）可通过这个方法来兼容，也可自行通过WebView.setDataDirectorySuffix()兼容
                // .webViewOtherProcessName(suffix)
                .build());
        mListener = listener;
    }

    @Override
    public void initAdOpenSwitch(String... isOpen) {
        try {
            ADLibConfig.AdOpenConfig.splashAdIsOpen = TextUtils.equals("1", isOpen[0]);
            ADLibConfig.AdOpenConfig.contentAdIsOpen = TextUtils.equals("1", isOpen[1]);
            ADLibConfig.AdOpenConfig.commentAdIsOpen = TextUtils.equals("1", isOpen[2]);
            ADLibConfig.AdOpenConfig.bannerAdIsOpen = TextUtils.equals("1", isOpen[3]);

            ADLibConfig.AdOpenConfig.itemAdIsOpen = TextUtils.equals("1", isOpen[4])
                    || TextUtils.equals("1", isOpen[5])
                    || TextUtils.equals("1", isOpen[6])
                    || TextUtils.equals("1", isOpen[7]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /******************************信息流广告模块*****************************/

    /**
     * 获取信息流广告
     * 第二三个参数待定,到时候内含段子可以调整
     *
     * @param activity
     * @return
     */
    @Override
    public ADInfoWarp initItemAd(Activity activity) {
        if (!ADLibConfig.AdOpenConfig.itemAdIsOpen) return null;
        if (adViewList == null) {
            adViewList = new ArrayList<>();
        }
        // 第三个参数是广告位序号（默认为0，用于支持单样式多广告位，无需要可以填0或者使用其他构造方法）
        ADMobGenInformation adMobGenInformation = new ADMobGenInformation(activity, InformationAdType.TWO_PIC_TWO_TEXT_FLOW, ADLibConfig.home_item);
        // 如果需要关闭按钮可以设置（默认是不开启的）
        // 设置广告曝光校验最小间隔时间(0~200)，默认为200ms，在RecyclerView或ListView这种列表中不建议设置更小值，在一些特定场景（如Dialog或者固定位置可根据要求设置更小值）
        // adMobGenInformation.setExposureDelay(200);

        adMobGenInformation.setListener(new SimpleADMobGenInformationAdListener() {
            @Override
            public void onADReceiv(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "信息流广告获取成功 ::::: ");
                adViewList.add(adMobGenInformation);
            }

            @Override
            public void onADExposure(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "广告展示曝光回调，但不一定是曝光成功了，比如一些网络问题导致上报失败 	::::: ");
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.item_type, BuriedPointListener.show);
                }
            }

            @Override
            public void onADClick(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "广告被点击 ::::: ");
//                AdHttpRequest.adCount(AdHttpRequest.item_click);
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.item_type, BuriedPointListener.click);
                }
            }

            @Override
            public void onADFailed(String error) {
                Log.i(TAG, "广告数据获取失败时回调 ::::: " + error);

            }

            // TODO: 2019-11-28 失败和渲染失败需要回调到列表隐藏处理
            @Override
            public void onADClose(IADMobGenInformation iadMobGenInformation) {
                // 不一定需要，如果setShowClose(true)了建议重写该回调方法
                Log.i(TAG, "广告关闭事件回调 ::::: ");
//                removeInformationAd(iadMobGenInformation);
            }

            @Override
            public void onADRenderFailed(IADMobGenInformation iadMobGenInformation) {
                // 渲染失败可以移除该广告对象
                Log.i(TAG, "onADRenderFailed: ");
//                removeInformationAd(iadMobGenInformation);
            }
        });
        adMobGenInformation.loadAd();
        adMobGenInformation.getInformationAdStyle().titleMarginRight(80);
        return ADInfoWarp.getInstance(adMobGenInformation);
    }


    /***********************************下面是启动页广告模块**********************/

    /**
     * SDK  提供的全屏API
     *
     * @param activity
     */
    public void fullScreen(Activity activity) {
        ADMobGenSDK.instance().fullScreen(activity);
    }

    public ADMobGenSplashView initSplashAd(Activity activity, AdSplashListenerAdapter listener, ViewGroup viewGroup) {
        if (!ADLibConfig.AdOpenConfig.splashAdIsOpen) return null;
        // 开屏广告高度默认为屏幕高度的75%,可自定义高度比例,但不能低于0.75;
        // 如activity_splash2.xml所示，如果底部logo想固定高度，并且能确开屏广告容器的高度是大于屏幕的75%的可以将参数设置为-1
        // 第三个参数是广告位序号（默认为0，用于支持单样式多广告位，无需要可以填0或者使用其他构造方法）
        ADMobGenSplashView adMobGenSplashView = new ADMobGenSplashView(activity, -1, 0);
        // 设置是否沉浸式（跳过按钮topMargin会加上状态栏高度），不设置默认false
        adMobGenSplashView.setImmersive(false);

        if (listener != null) {
            listener.setPointListener(getBuriedPointListener());
        }
        // 设置开屏广告监听
        adMobGenSplashView.setListener(listener);
        viewGroup.addView(adMobGenSplashView);
        adMobGenSplashView.loadAd();
        return adMobGenSplashView;
    }


    public void onSplashDestroy(ADMobGenSplashView adMobGenSplashView) {
        if (adMobGenSplashView != null) {
            adMobGenSplashView.destroy();
            adMobGenSplashView = null;
        }
    }


    /******************************下面是banner 广告*******************************/


    @Override
    public ADInfoWarp initBannerAd(Activity activity, final CommentDateCallBack callBack) {
        if (!ADLibConfig.AdOpenConfig.bannerAdIsOpen) return null;

        // 第三个参数是广告位序号（默认为0，用于支持单样式多广告位，无需要可以填0或者使用其他构造方法）
        ADMobGenInformation adMobGenInformation = new ADMobGenInformation(activity, InformationAdType.ONLY_IMAGE);
        // 如果需要关闭按钮可以设置（默认是不开启的）
        // 设置广告曝光校验最小间隔时间(0~200)，默认为200ms，在RecyclerView或ListView这种列表中不建议设置更小值，在一些特定场景（如Dialog或者固定位置可根据要求设置更小值）
        // adMobGenInformation.setExposureDelay(200);

        adMobGenInformation.setListener(new SimpleADMobGenInformationAdListener() {
            @Override
            public void onADReceiv(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "banner  信息流广告获取成功 ::::: ");
                View adView = adMobGenInformation.getInformationAdView();

                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                adView.setTag(adMobGenInformation);

                if (callBack != null) {
                    callBack.commentAd(adView);
                }

            }

            @Override
            public void onADExposure(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "banner  广告展示曝光回调，但不一定是曝光成功了，比如一些网络问题导致上报失败 	::::: ");
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.banner_type, BuriedPointListener.show);
                }
            }

            @Override
            public void onADClick(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "banner  广告被点击 ::::: ");
//                AdHttpRequest.adCount(AdHttpRequest.item_click);
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.banner_type, BuriedPointListener.click);
                }
            }

            @Override
            public void onADFailed(String error) {
                Log.i(TAG, "banner  广告数据获取失败时回调 ::::: " + error);
                if (callBack != null) {
                    callBack.remove();
                }

            }

            @Override
            public void onADRenderFailed(IADMobGenInformation iadMobGenInformation) {
                // 渲染失败可以移除该广告对象
                Log.i(TAG, "banner  onADRenderFailed: ");
                if (callBack != null) {
                    callBack.remove();
                }
            }
        });
        adMobGenInformation.loadAd();
        return ADInfoWarp.getInstance(adMobGenInformation);
    }


    /*****************************  详情头广告 ********************************/
    @Override
    public ADInfoWarp initDetailHeaderAd(Activity activity, final CommentDateCallBack callBack) {
        if (!ADLibConfig.AdOpenConfig.contentAdIsOpen) return null;

        // 第三个参数是广告位序号（默认为0，用于支持单样式多广告位，无需要可以填0或者使用其他构造方法）
        ADMobGenInformation adMobGenInformation = new ADMobGenInformation(activity, InformationAdType.LEFT_IMAGE);
        // 如果需要关闭按钮可以设置（默认是不开启的）
        // 设置广告曝光校验最小间隔时间(0~200)，默认为200ms，在RecyclerView或ListView这种列表中不建议设置更小值，在一些特定场景（如Dialog或者固定位置可根据要求设置更小值）
        // adMobGenInformation.setExposureDelay(200);
        adMobGenInformation.setShowClose(true);

        adMobGenInformation.setListener(new SimpleADMobGenInformationAdListener() {
            @Override
            public void onADReceiv(IADMobGenInformation adMobGenInformation) {
                if (callBack != null) {
                    Log.i(TAG, "详情头布局  信息流广告获取成功 ::::: ");
                    View adView = adMobGenInformation.getInformationAdView();
                    adView.setTag(adMobGenInformation);
                    callBack.commentAd(adView);
                }
            }

            @Override
            public void onADExposure(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "详情头布局  广告展示曝光回调，但不一定是曝光成功了，比如一些网络问题导致上报失败 	::::: ");
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.detail_type, BuriedPointListener.show);
                }
            }

            @Override
            public void onADClick(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "详情头布局  广告被点击 ::::: ");
//                AdHttpRequest.adCount(AdHttpRequest.item_click);
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.detail_type, BuriedPointListener.click);
                }
            }

            @Override
            public void onADFailed(String error) {
                Log.i(TAG, "详情头布局  广告数据获取失败时回调 ::::: " + error);

            }

            @Override
            public void onADClose(IADMobGenInformation iadMobGenInformation) {
                Log.i(TAG, "详情头布局  广告关闭");
                if (callBack != null) {
                    callBack.remove();
                }

            }

            @Override
            public void onADRenderFailed(IADMobGenInformation iadMobGenInformation) {
                // 渲染失败可以移除该广告对象
                Log.i(TAG, "详情头布局  onADRenderFailed: ");
//                removeInformationAd(iadMobGenInformation);
            }
        });
        adMobGenInformation.loadAd();
        return ADInfoWarp.getInstance(adMobGenInformation);
    }

    /************************** 评论列表 ************************************/

    @Override
    public ADInfoWarp initCommentItemAd(Activity activity, final CommentDateCallBack callBack) {
        if (!ADLibConfig.AdOpenConfig.commentAdIsOpen) return null;

        // 第三个参数是广告位序号（默认为0，用于支持单样式多广告位，无需要可以填0或者使用其他构造方法）
        ADMobGenInformation adMobGenInformation = new ADMobGenInformation(activity, InformationAdType.TWO_PIC_TWO_TEXT_FLOW, ADLibConfig.comment_item);
        // 如果需要关闭按钮可以设置（默认是不开启的）
        // 设置广告曝光校验最小间隔时间(0~200)，默认为200ms，在RecyclerView或ListView这种列表中不建议设置更小值，在一些特定场景（如Dialog或者固定位置可根据要求设置更小值）
        // adMobGenInformation.setExposureDelay(200);
        // TODO: 2019-11-29 关闭需要自己处理,需要知道position
//        adMobGenInformation.setShowClose(true);

        adMobGenInformation.setListener(new SimpleADMobGenInformationAdListener() {
            @Override
            public void onADReceiv(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "评论列表  信息流广告获取成功 ::::: ");
                if (callBack != null) {
                    View adView = adMobGenInformation.getInformationAdView();
                    adView.setTag(adMobGenInformation);
                    callBack.commentAd(adView);
                }
            }

            @Override
            public void onADExposure(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "评论列表  广告展示曝光回调，但不一定是曝光成功了，比如一些网络问题导致上报失败 	::::: ");
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.comment_type, BuriedPointListener.show);
                }
            }

            @Override
            public void onADClick(IADMobGenInformation adMobGenInformation) {
                Log.i(TAG, "评论列表  广告被点击 ::::: ");
                if (mListener != null) {
                    mListener.adItemBuriedPoint(BuriedPointListener.comment_type, BuriedPointListener.click);
                }
            }

            @Override
            public void onADClose(IADMobGenInformation iadMobGenInformation) {
                Log.i(TAG, "评论列表  广告关闭");
            }

            @Override
            public void onADFailed(String error) {
                Log.i(TAG, "评论列表  广告数据获取失败时回调 ::::: " + error);

            }

            @Override
            public void onADRenderFailed(IADMobGenInformation iadMobGenInformation) {
                // 渲染失败可以移除该广告对象
                Log.i(TAG, "评论列表  onADRenderFailed: ");
//                removeInformationAd(iadMobGenInformation);
            }
        });
        adMobGenInformation.getInformationAdStyle().titleMarginRight(80);
        adMobGenInformation.loadAd();
        return ADInfoWarp.getInstance(adMobGenInformation);
    }


    /**
     * 获取广告是在处理数据时调用该方法
     * 这个聚合SDK 需要通过 IADMobGenInformation 来刷新广告view
     * 专门用于信息流列表广告的获取
     *
     * @param adMobGenInformation
     * @return
     */
    @Override
    public View getAdView(ADInfoWarp adMobGenInformation) {
        if (adViewList == null || count > adViewList.size() - 1) return null;
        IADMobGenInformation information = adViewList.get(count);
        View informationAdView = information.getInformationAdView();
        informationAdView.setTag(information);

        count++;
        loadAd(adMobGenInformation);
        Log.i(TAG, "getAdDate: 拿广告");
        return informationAdView;
    }

    /**
     * 获取详情头布局广告
     * count++ 操作需要再外面处理
     *
     * @param adMobGenInformation
     * @return
     */
    public View getDetailAd(ADInfoWarp adMobGenInformation, View adView) {
        if (adView == null || adMobGenInformation == null)
            return null;
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        loadAd(adMobGenInformation);
        Log.i(TAG, "详情头布局: 拿广告");
        return adView;
    }

    /**
     * 显示头布局可以共用
     *
     * @param adView
     * @param adContainer
     */
    @Override
    public void showAD(View adView, ViewGroup adContainer) {
        if (adView == null) {
            adContainer.removeAllViews();
            return;
        }
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }

        adContainer.removeAllViews();
        adContainer.addView(adView);
        //这个是真的不人性,不调用就没有广告
        Object tag = adView.getTag();
        if (tag instanceof IADMobGenInformation) {
            ((IADMobGenInformation) tag).render();
        }
        Log.i(TAG, "showAD: 展示广告");
    }

    /**
     * 专门用于信息流列表广告的加载
     *
     * @param adMobGenInformation
     */
    @Override
    public void loadAd(ADInfoWarp adMobGenInformation) {
        if (adMobGenInformation == null) return;
        adMobGenInformation.loadAd();
    }


    /**
     * 首页退出调用
     *
     * @param information
     */
    @Override
    public void destroy(ADInfoWarp information) {
        if (information != null) {
            count = 0;
            adViewList.clear();
            adViewList = null;
            information.destory();
            information = null;
        }
    }
}
