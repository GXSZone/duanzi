package com.caotu.duanzhi.utils;

import android.content.Context;
import android.util.Log;

import com.caotu.duanzhi.Http.RealmBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * 数据库的操作类
 * https://realm.io/cn/docs/java/latest/#%E8%81%9A%E5%90%88
 */
public final class RealmHelper {
    private static final String TAG = "RealmHelper";

    public static void init(Context context) {
        //初始化数据库
        Realm.init(context);
        //方便调试和查看数据库内容

//        Stetho.initialize(Stetho.newInitializerBuilder(context)
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
//                .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
//                .build());
    }

    public static void insertOrUpdate(String contentID) {
        Realm realm = Realm.getDefaultInstance();

        RealmBean content = new RealmBean();
        content.setContentId(contentID);
        content.setTime(System.currentTimeMillis());
        //异步事务
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.insertOrUpdate(content);
//            }
//        });
        //同步事务
        realm.beginTransaction();
        realm.insertOrUpdate(content);
        realm.commitTransaction();
        Log.i(TAG, "insertOrUpdate: " + contentID);
    }

    /**
     * 获取排好序的集合数据
     *
     * @return
     */
    public static List<RealmBean> getSortedList() {
        Realm instance = Realm.getDefaultInstance();
        RealmResults<RealmBean> allAsync = instance.where(RealmBean.class).findAllAsync();
        //这把很关键,需要重新赋值
        allAsync = allAsync.sort("time", Sort.DESCENDING);
        List<RealmBean> beans = instance.copyFromRealm(allAsync);
        //调试获取数据
        if (AppUtil.listHasDate(beans)) {
            for (int i = 0; i < beans.size(); i++) {
                Log.i(TAG, beans.get(i).getContentId());
            }
        }
        Log.i(TAG, "getSortedList: "+beans.size());
        return beans;
    }

    /**
     * 清空所有数据
     */
    public static void clearAll() {
        Realm instance = Realm.getDefaultInstance();
        RealmResults<RealmBean> users = instance.where(RealmBean.class).findAll();
        users.deleteAllFromRealm();
    }

    /**
     * 为了兼容老版本,从SP 获取历史记录存入数据库中
     * 之前留下了坑,存的时候是以hashMap存的,需要转成集合的形式
     */
    public static void putDateFromSp() {
        Realm instance = Realm.getDefaultInstance();
        RealmResults<RealmBean> all = instance.where(RealmBean.class).findAll();
        // TODO: 2019-11-25 如果数据库已经有数据则不再处理
        if (all != null && all.size() > 0) {
            return;
        }
        HashMap<String, Long> data = MySpUtils.getHashMapData();
        if (data == null) return;
        List<RealmBean> list = new ArrayList<>(data.size());
        Set<Map.Entry<String, Long>> entries = data.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            RealmBean bean = new RealmBean();
            bean.setContentId(entry.getKey());
            bean.setTime(entry.getValue());
        }
        MySpUtils.deleteKey(MySpUtils.SP_LOOK_HISTORY);

        instance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: ");
            }
        });
    }
}
