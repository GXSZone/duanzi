package com.caotu.duanzhi.Http;

import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.UserFansBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/11 17:20
 */
public class DataTransformUtils {
    /**
     * 我的关注列表项数据转换,包括话题和用户
     * @param initialData
     * @param isMe
     * @param isTheme
     * @return
     */
    public static List<ThemeBean> getMyFocusDataBean(List<UserFocusBean.RowsBean> initialData, boolean isMe, boolean isTheme) {
        List<ThemeBean> resultData = new ArrayList<>();
        for (UserFocusBean.RowsBean row : initialData) {
            ThemeBean themeBean = new ThemeBean();
            boolean isfocus;
            //如果是专栏,直接判断,如果是跟用户相关则需要考虑两个字段
            if (isTheme) {
                isfocus = "1".equals(row.getIsfollow());
            } else {
                if (isMe) {
                    isfocus = "1".equals(row.getEachotherflag());
                } else {
                    if ("1".equals(row.getEachotherflag())) {
                        isfocus = true;
                    } else {
                        isfocus = "1".equals(row.getIsfollow());
                    }
                }
            }
            themeBean.setFocus(isfocus);
            themeBean.setMe(isMe);
            themeBean.setTheme(isTheme);
            if (isTheme) {
                themeBean.setThemeAvatar(row.getTagimg());
                themeBean.setThemeName(row.getTagalias());
                themeBean.setUserId(row.getTagid());
                themeBean.setThemeSign(row.getTaglead());
            } else {
                themeBean.setThemeAvatar(row.getUserheadphoto());
                themeBean.setThemeName(row.getUsername());
                themeBean.setUserId(row.getUserid());
                themeBean.setThemeSign(row.getUsersign());
            }
            resultData.add(themeBean);
        }
        return resultData;
    }


    /**
     * 粉丝列表项数据转换(他人页面的粉丝和我个人页面的粉丝逻辑不一样)
     */
    public static List<ThemeBean> getMyFansDataBean(List<UserFansBean.RowsBean> initialData, boolean isMe) {
        List<ThemeBean> resultData = new ArrayList<>(initialData.size());
        for (UserFansBean.RowsBean row : initialData) {
            ThemeBean themeBean = new ThemeBean();
            boolean isfocus;
            if ("1".equals(row.getEachotherflag())) {
                isfocus = true;
            } else {
                isfocus = !"0".equals(row.getIsfollow());
            }
            themeBean.setFocus(isfocus);
            themeBean.setMe(isMe);
            themeBean.setThemeAvatar(row.getUserheadphoto());
            themeBean.setThemeName(row.getUsername());
            themeBean.setUserId(row.getUserid());
            themeBean.setThemeSign(row.getUsertype());
            resultData.add(themeBean);
        }
        return resultData;
    }
}
