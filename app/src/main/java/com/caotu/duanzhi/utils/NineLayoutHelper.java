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

    public static int getMaxImgWidth() {
        return maxImgWidth;
    }

    public static int getCellWidth() {
        return cellWidth;
    }

    public static int getCellHeight() {
        return cellHeight;
    }

    public static int getMargin() {
        return margin;
    }



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

    public GridLayoutHelper getLayoutHelper(List<ImageData> list) {
        int spanCount = Utils.getSize(list);
        if (spanCount == 1) {
            int width = list.get(0).realWidth;
            int height = list.get(0).realHeight;
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

        if (spanCount > 3) {
            spanCount = (int) Math.ceil(Math.sqrt(spanCount));
        }

        if (spanCount > 3) {
            spanCount = 3;
        }
        return new GridLayoutHelper(spanCount, cellWidth, cellHeight, margin);
    }
}
