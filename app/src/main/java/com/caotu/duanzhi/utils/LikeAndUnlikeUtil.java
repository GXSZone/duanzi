package com.caotu.duanzhi.utils;

import android.text.TextUtils;

/**
 * @author mac
 * @日期: 2018/11/14
 * @describe TODO
 */
public class LikeAndUnlikeUtil {
    /**
     * 判断是否已经点了赞,针对只有一个💕点赞按钮
     *
     * @param goodstatus
     * @return
     */
    public static boolean isLiked(String goodstatus) {
        return TextUtils.equals("1", goodstatus);
    }

    public static boolean isVideoType(String type) {
        return TextUtils.equals("1", type) || TextUtils.equals("2", type);
    }
}
