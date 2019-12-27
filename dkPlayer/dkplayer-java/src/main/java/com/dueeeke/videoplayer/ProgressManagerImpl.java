package com.dueeeke.videoplayer;

import android.text.TextUtils;
import android.util.LruCache;

import com.dueeeke.videoplayer.player.ProgressManager;

public class ProgressManagerImpl extends ProgressManager {

    //保存100条记录
    private static LruCache<Integer, Long> mCache = new LruCache<>(10);

    @Override
    public void saveProgress(String url, long progress) {
        if (TextUtils.isEmpty(url)) return;
        if (progress == 0) {
            clearSavedProgressByUrl(url);
            return;
        }
        mCache.put(url.hashCode(), progress);
    }

    @Override
    public long getSavedProgress(String url) {
        if (TextUtils.isEmpty(url)) return 0;
        Long pro = mCache.get(url.hashCode());
        if (pro == null) return 0;
        return pro;
    }

    public void clearAllSavedProgress() {
        mCache.evictAll();
    }

    public void clearSavedProgressByUrl(String url) {
        mCache.remove(url.hashCode());
    }
}
