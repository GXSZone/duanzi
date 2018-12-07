package com.caotu.duanzhi.Http;


import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.SelectThemeDataBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.Http.bean.UserFansBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.utils.VideoAndFileUtils;

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
     *
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
                themeBean.setAuth(row.getAuth());
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
            themeBean.setAuth(row.getAuth());
            resultData.add(themeBean);
        }
        return resultData;
    }

    /**
     * 用于转换话题对象
     *
     * @param rowsBeanList
     * @return
     */
    public static List<TopicItemBean> summaryTopicBean(List<SelectThemeDataBean.RowsBean> rowsBeanList) {
        List<TopicItemBean> beanList = new ArrayList<>();
        for (SelectThemeDataBean.RowsBean bean : rowsBeanList) {
            List<SelectThemeDataBean.RowsBean.TagsBean> taglist = bean.getTaglist();
            for (SelectThemeDataBean.RowsBean.TagsBean tagsBean : taglist) {
                TopicItemBean bean1 = new TopicItemBean();
                bean1.setTagalias(tagsBean.getTagalias());
                bean1.setTagid(tagsBean.getTagid());
                bean1.setTagimg(tagsBean.getTagimg());
                beanList.add(bean1);
            }
        }
        return beanList;
    }

    /**
     * Ugc内容展示在评论列表的操作
     *
     * @param notice
     * @return
     */
    public static CommendItemBean.RowsBean changeUgcBean(MomentsDataBean notice) {
        CommendItemBean.RowsBean momentsDataBean = new CommendItemBean.RowsBean();
        try {
            momentsDataBean.contentid = notice.getContentid();
            momentsDataBean.username = notice.getUsername();
            momentsDataBean.userheadphoto = notice.getUserheadphoto();
            momentsDataBean.goodstatus = notice.getGoodstatus();
            momentsDataBean.commenttext = notice.getContenttitle();
            momentsDataBean.commentgood = notice.getContentgood();
            //userId的赋值
            momentsDataBean.userid = notice.getContentuid();

            momentsDataBean.commenturl = VideoAndFileUtils.changeStringToCommentUrl(
                    notice.getContenturllist(), notice.getContenttype());
            momentsDataBean.replyCount = notice.getContentcomment();
            //这个字段很关键,都是根据这个字段区别,用于点赞的接口请求区别
            momentsDataBean.isUgc = true;
            momentsDataBean.commentid = notice.getContentid();
            momentsDataBean.isShowTitle = !"1".equals(notice.getIsshowtitle());
            MomentsDataBean.BestmapBean bestmap = notice.getBestmap();
            if (bestmap != null && !TextUtils.isEmpty(bestmap.getCommentid())) {
                ArrayList<CommendItemBean.ChildListBean> beans = new ArrayList<>();
                CommendItemBean.ChildListBean childBean = new CommendItemBean.ChildListBean();
                childBean.commenttext = bestmap.getCommenttext();
                childBean.commenturl = bestmap.getCommenturl();
                childBean.username = bestmap.getUsername();
                childBean.userid = bestmap.getUserid();
                childBean.commentid = bestmap.getCommentid();
                beans.add(childBean);
                momentsDataBean.childList = beans;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return momentsDataBean;
    }
}
