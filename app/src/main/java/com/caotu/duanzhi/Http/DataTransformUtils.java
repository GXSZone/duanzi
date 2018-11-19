package com.caotu.duanzhi.Http;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.SelectThemeDataBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
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
     * 通知页面跳转详情bean对象封装
     *
     * @param notice
     * @return
     */
    public static MomentsDataBean getBeanSkipDeatil(MessageDataBean.RowsBean.ContentBean notice) {
        MomentsDataBean momentsDataBean = new MomentsDataBean();
        try {
            momentsDataBean.setContentcomment(Integer.parseInt(notice.contentcomment));
            momentsDataBean.setContentid(notice.contentid);
            momentsDataBean.setContenttext(notice.contenttext);
            momentsDataBean.setContenttitle(notice.contenttitle);
            momentsDataBean.setContenttype(notice.contenttype);
            momentsDataBean.setContenturllist(notice.contenturllist);
            momentsDataBean.setIsfollow(notice.isfollow);
            momentsDataBean.setTagshow(notice.tagshow);
            momentsDataBean.setTagshowid(notice.tagshowid);
            momentsDataBean.setContentgood(Integer.parseInt(notice.contentgood));
            momentsDataBean.setContentbad(Integer.parseInt(notice.contentbad));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return momentsDataBean;
    }

    /**
     * 评论列表跳转评论详情的bean对象转换
     *
     * @param notice
     * @return
     */
    public static CommendItemBean.RowsBean getBeanComment(CommentBaseBean.RowsBean notice) {
        CommendItemBean.RowsBean momentsDataBean = new CommendItemBean.RowsBean();
        /*
          public List<ChildListBean> childList;
        public boolean isBest;//是不是热门评论
        public boolean showHeadr;//显示头部
        public boolean isUgc; //是否是UGC内容,跳转方式不一样
        public int pageno;
        public int pagesize;
        public int start;
        public String commentid;
        public String userid;
        public String contentid;
        public int commentreply;
        public String commenttext;
        public String replycomment;
        public int commentgood;
        public String username;
        public String ruusername;
        public String ruuserid;
        public String userheadphoto;
        public String replyfirst;
        public String createtime;
        public int replyCount;
        public int isgood;
        //[{"cover": "资源封面URL", "type": 1横视频2竖视频3图片4GIF, "info": "资源URL"}]
        public String commenturl;
        //"0"_未赞未踩 "1"_已赞 "2"_已踩
        public String goodstatus;
        public String isfollow;
         */

        try {
            momentsDataBean.commentid = notice.commentid;
            momentsDataBean.username = notice.username;
            momentsDataBean.contentid = notice.contentid;
            momentsDataBean.commentgood = notice.commentgood;

            momentsDataBean.userheadphoto = notice.userheadphoto;
            momentsDataBean.commenturl = notice.commenturl;
            momentsDataBean.userid = notice.userid;
            momentsDataBean.commenttext = notice.commenttext;
            momentsDataBean.isgood = Integer.parseInt(notice.isgood);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return momentsDataBean;
    }
}
