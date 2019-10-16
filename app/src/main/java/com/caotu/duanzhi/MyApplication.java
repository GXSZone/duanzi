package com.caotu.duanzhi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.caotu.duanzhi.view.NewRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 可优化点:
 * 图片插件压缩 :https://github.com/duking666/ImgCompressPlugin/blob/master/README-zh-rCN.md
 */
public class MyApplication extends Application {
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            return new NewRefreshHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }
    /**
     * 全局handler
     */
    private Handler handler;


    public void putHistory(String contentId) {
       ContextProvider.get().putHistory(contentId);
    }

    public static MyApplication getInstance() {
        return ContextProvider.get().getApplication();
    }


    /**
     * 文件地址转换,节省流量,首页列表的图片和视频都要替换
     * 只有图片类型才有这玩意,视频封面没有,这锤子操作也是服气,注释代码看看就好,用不上
     *
     * @param url
     * @return
     */
    public static String buildFileUrl(String url) {
        if (TextUtils.isEmpty(url)) return url;
        if (url.contains("cos.ap-shanghai.myqcloud")) {
            url = url.replace("cos.ap-shanghai.myqcloud", "file.myqcloud");
        }
        if (url.startsWith("https")) {
            url = url.replace("https", "http");
        }
        //图片替换
//        if (url.endsWith(".png")) {
//            url = url.replace(".png", ".small.png");
//        }
//        if (url.endsWith(".jpg")) {
//            url = url.replace(".jpg", ".small.jpg");
//        }
//
//        if (url.endsWith(".jpeg")) {
//            url = url.replace(".jpeg", ".small.jpeg");
//        }
//
//        if (url.endsWith(".gif")) {
//            url = url.replace(".gif", ".small.gif");
//        }

        return url;
    }


    /*=======================================自定义Activity栈   START==========================================*/

    public Activity getLastSecondActivity() {
        return ContextProvider.get().getLastSecondActivity();
    }

    /**
     * 获取栈顶activity,全局可以使用,省去传context的麻烦和空指针的问题
     * java.util.NoSuchElementException ,如果为空的话是直接报错的,getLast 这个api
     *
     * @return
     */
    public Activity getRunningActivity() {
      return ContextProvider.get().getRunningActivity();
    }

    /*=======================================自定义Activity栈 END==========================================*/

    /**
     * 全局一个handler用来处理子线程和主线程问题
     *
     * @return
     */
    public Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }


    /**
     * 这个bug 之前修改过,注释了就又来了,不能删了
     * #9105 java.util.concurrent.TimeoutException
     * android.content.res.AssetManager.finalize() timed out after 120 seconds
     * <p>
     * android.content.res.AssetManager.destroy(Native Method)
     */
    public static void fix() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        fix();
    }
}
