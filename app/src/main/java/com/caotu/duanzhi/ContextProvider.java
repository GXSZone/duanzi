package com.caotu.duanzhi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.home.MainActivity;

import java.util.LinkedList;

/**
 * 代替之前的application包含的方法,都隔离到这里,解决加固和热修复的冲突问题,都会去修改底层的application
 */
public class ContextProvider {
    @SuppressLint("StaticFieldLeak")
    private static volatile ContextProvider instance;
    private Context mContext;
    public static final LinkedList<Activity> activities = new LinkedList<>();
    private int rebusActivists = 0;

    private ContextProvider(Context context) {
        mContext = context;
    }

    /**
     * 获取实例
     */
    public static ContextProvider get() {
        if (instance == null) {
            synchronized (ContextProvider.class) {
                if (instance == null) {
                    Context context = ApplicationContextProvider.mContext;
                    if (context == null) {
                        throw new IllegalStateException("context == null");
                    }
                    instance = new ContextProvider(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return mContext;
    }

    public MyApplication getApplication() {
        return (MyApplication) mContext.getApplicationContext();
    }

    public void addActivity(Activity activity) {
        activities.addLast(activity);
    }

    public void remove(Activity activity) {
        activities.remove(activity);
    }

    public void addCount(Activity activity) {
        rebusActivists++;
    }

    public void removeCount(Activity activity) {
        rebusActivists--;
        if (rebusActivists == 0) {
            //计时器查询
            if (getBottomActivity() != null && getBottomActivity() instanceof MainActivity) {
                ((MainActivity) getBottomActivity()).stopHandler();
            }
        }
    }

    /**
     * 获取栈顶activity,全局可以使用,省去传context的麻烦和空指针的问题
     * java.util.NoSuchElementException ,如果为空的话是直接报错的,getLast 这个api
     *
     * @return
     */
    public Activity getRunningActivity() {
        Activity last = null;
        try {
            last = activities.getLast();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return last;
    }

    public Activity getLastSecondActivity() {
        if (activities.size() >= 2) {
            return activities.get(activities.size() - 2);
        } else {
            return null;
        }
    }

    public Activity getBottomActivity() {
        return activities.getFirst();
    }


    public void setBrightness(boolean isChecked) {
        if (activities == null || activities.isEmpty()) return;
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).setBrightness(isChecked);
            }
        }
    }
}
