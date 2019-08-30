package com.caotu.duanzhi.Http;


import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.SelectThemeDataBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.UserFansBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
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
     * 把一些数据的处理放到接口请求回来后直接处理,不在列表展示再处理------尝试
     *
     * @param list
     * @return
     */
    public static List<MomentsDataBean> getContentNewBean(List<MomentsDataBean> list) {
        if (list == null || list.isEmpty()) return list;
        for (MomentsDataBean momentsDataBean : list) {
            momentsDataBean.imgList = VideoAndFileUtils.getImgList(momentsDataBean.getContenturllist(),
                    momentsDataBean.getContenttext());
            momentsDataBean.isMySelf = MySpUtils.isMe(momentsDataBean.getContentuid());
            momentsDataBean.isShowCheckAll = calculateShowCheckAllText(momentsDataBean.getContenttitle());
            AuthBean auth = momentsDataBean.getAuth();
            if (auth != null) {
                momentsDataBean.authPic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
        }
        return list;
    }

    public static MomentsDataBean getContentNewBean(MomentsDataBean bean) {
        if (bean == null) return null;
        bean.imgList = VideoAndFileUtils.getImgList(bean.getContenturllist(),
                bean.getContenttext());
        bean.isMySelf = MySpUtils.isMe(bean.getContentuid());
        bean.isShowCheckAll = calculateShowCheckAllText(bean.getContenttitle());
        AuthBean auth = bean.getAuth();
        if (auth != null) {
            bean.authPic = VideoAndFileUtils.getCover(auth.getAuthpic());
        }
        return bean;
    }

    public static boolean calculateShowCheckAllText(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        // TODO: 2019-04-25 下面的文字长度计算是不会统计到的,只有通过匹配切割的方式判断换行符的个数
        String[] split = content.split("\\n");
        if (split.length > 7) {
            return true;
        }
        Paint textPaint = new Paint();
        textPaint.setTextSize(DevicesUtils.dp2px(16f));
        float textWidth = textPaint.measureText(content);
        float maxContentViewWidth = DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(60f);
        float maxLines = textWidth / maxContentViewWidth;
        Log.i("maxText", "textWidth: " + textWidth + "----maxContentViewWidth:" + maxContentViewWidth
                + "------maxLines:" + maxLines);
        return maxLines > 8;
    }

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
    public static List<ThemeBean> getMyFansDataBean(List<UserFansBean.RowsBean> initialData, boolean isMe, boolean isNeedBreak) {
        List<ThemeBean> resultData = new ArrayList<>(initialData.size());
        for (int i = 0; i < initialData.size(); i++) {
            //数据裁剪,为了隐藏一些马甲账号显示
            if (isNeedBreak) {
                if (i == 10) break;
            }
            UserFansBean.RowsBean row = initialData.get(i);
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
            momentsDataBean.createtime = notice.getCreatetime();
            momentsDataBean.auth = notice.getAuth();
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

    /**
     * 接口返回的关注对象和搜索用户那边返回的用户对象不一致,所以为了统一都用搜索用户的对象,字段少一些
     *
     * @param list
     * @param hasHeader
     * @return
     */
    public static List<UserBean> changeFocusUserToAtUser(List<UserFocusBean.RowsBean> list, boolean hasHeader) {
        if (list == null || list.isEmpty())
            return null;
        ArrayList<UserBean> beanArrayList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            UserFocusBean.RowsBean rowsBean = list.get(i);
            UserBean bean = new UserBean();
            AuthBean auth = rowsBean.getAuth();
            if (auth != null) {
                bean.authpic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
            bean.userid = rowsBean.getUserid();
            bean.username = rowsBean.getUsername();
            bean.userheadphoto = rowsBean.getUserheadphoto();
            if (i == 0 && hasHeader) {
                bean.isHeader = true;
            }
            bean.isFocus = true;
            beanArrayList.add(bean);

        }
        return beanArrayList;
    }

    /**
     * 搜索出来的@ 用户集合转换,也可以用于正常用户
     *
     * @param list
     * @return
     */
    public static List<UserBean> changeSearchUserToAtUser(List<UserBaseInfoBean.UserInfoBean> list) {
        if (list == null || list.isEmpty())
            return null;
        ArrayList<UserBean> beanArrayList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            UserBaseInfoBean.UserInfoBean rowsBean = list.get(i);
            UserBean bean = new UserBean();
            AuthBean auth = rowsBean.getAuth();
            if (auth != null) {
                bean.authpic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
            bean.userid = rowsBean.getUserid();
            bean.username = rowsBean.getUsername();
            bean.userheadphoto = rowsBean.getUserheadphoto();
            beanArrayList.add(bean);

        }
        return beanArrayList;
    }
}
