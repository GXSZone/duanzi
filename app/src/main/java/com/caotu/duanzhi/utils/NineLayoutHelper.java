package com.caotu.duanzhi.utils;

import com.sunfusheng.util.Utils;
import com.sunfusheng.widget.GridLayoutHelper;
import com.sunfusheng.widget.ImageData;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe TODO
 */
public class NineLayoutHelper {

    private static final NineLayoutHelper ourInstance = new NineLayoutHelper();
    private static boolean isInit = false;

    private static int maxImgWidth;
    private static int maxImgHeight;
    private static int cellWidth;
    private static int cellHeight;
    private static int minImgWidth;
    private static int minImgHeight;
    private static int margin;


    public static NineLayoutHelper getInstance() {
        if (!isInit) {
            margin = DevicesUtils.dp2px(3);
            maxImgHeight = maxImgWidth = (DevicesUtils.getSrecchWidth());
//                                - Utils.dp2px(mContext, 16) * 2);
            cellHeight = cellWidth = (maxImgWidth - margin * 3) / 3;
            minImgHeight = minImgWidth = cellWidth;
            isInit = true;
        }

        return ourInstance;
    }

    private NineLayoutHelper() {
    }

    /**
     * 神评和评论列表还是原先的逻辑
     * @param list
     * @return
     */
    public GridLayoutHelper getLayoutHelper(List<ImageData> list) {
        int spanCount = Utils.getSize(list);
        int width;
        int height;
        if (spanCount == 1) {
            width = list.get(0).realWidth;
            height = list.get(0).realHeight;
            if (width > 0 && height > 0) {
                float whRatio = width * 1f / height;
                if (width > height) {
                    width = Math.max(minImgWidth, Math.min(width, maxImgWidth));
                    height = Math.max(minImgHeight, (int) (width / whRatio));
                } else {
                    height = Math.max(minImgHeight, Math.min(height, maxImgHeight));
                    width = Math.max(minImgWidth, (int) (height * whRatio));
                }
            } else {
                width = cellWidth;
                height = cellHeight;
            }
            return new GridLayoutHelper(spanCount, width, height, margin);
        }

        // TODO: 2019-06-11 2/4 张图铺满屏幕
        if (spanCount == 2 || spanCount == 4) {
            spanCount = 2;
            width = height = (maxImgWidth - margin) / 2;
        } else {
            if (spanCount > 3) {
                spanCount = (int) Math.ceil(Math.sqrt(spanCount));
            }

            if (spanCount > 3) {
                spanCount = 3;
            }
            width = height = cellHeight;
        }

        return new GridLayoutHelper(spanCount, width, height, margin);
    }

    /**
     * 内容单图宽大与高的时候充满屏幕
     * @param list
     * @return
     */
    public GridLayoutHelper getContentLayoutHelper(List<ImageData> list) {
        int spanCount = Utils.getSize(list);
        int width;
        int height;
        if (spanCount == 1) {
            width = list.get(0).realWidth;
            height = list.get(0).realHeight;
            if (width > 0 && height > 0) {
                float whRatio = width * 1f / height;
                if (width > height) {
                    width = maxImgWidth - DevicesUtils.dp2px(40);
                    height = Math.max(minImgHeight, (int) (width / whRatio));
                } else {
                    height = Math.max(minImgHeight, Math.min(height, maxImgHeight));
                    width = Math.max(minImgWidth, (int) (height * whRatio));
                }
            } else {
                width = cellWidth;
                height = cellHeight;
            }
            return new GridLayoutHelper(spanCount, width, height, margin);
        }

        // TODO: 2019-06-11 2/4 张图铺满屏幕
        if (spanCount == 2 || spanCount == 4) {
            spanCount = 2;
            width = height = (maxImgWidth - margin) / 2;
        } else {
            if (spanCount > 3) {
                spanCount = (int) Math.ceil(Math.sqrt(spanCount));
            }

            if (spanCount > 3) {
                spanCount = 3;
            }
            width = height = cellHeight;
        }

        return new GridLayoutHelper(spanCount, width, height, margin);
    }
}
