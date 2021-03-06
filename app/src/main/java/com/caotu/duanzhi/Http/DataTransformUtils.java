package com.caotu.duanzhi.Http;


import android.app.Activity;
import android.graphics.Paint;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.TopicInfoBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.detail_scroll.ContentNewDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/11 17:20
 */
public class DataTransformUtils {
    /**
     * 把一些数据的处理放到接口请求回来后直接处理,不在列表展示再处理------尝试
     * 广告view 设置放在数据处理可以在列表里直接获取使用
     *
     * @param list
     * @return
     */
    public static List<MomentsDataBean> getContentNewBean(List<MomentsDataBean> list) {
        if (list == null || list.isEmpty()) return list;
        // TODO: 2019-11-04 这样会有问题,详情滑动列表获取数据,广告位就拿不到了
        Activity activity = MyApplication.getInstance().getRunningActivity();
        Activity secondActivity = MyApplication.getInstance().getLastSecondActivity();
        Activity runActivity = activity;
        boolean isMain = false;
        if (activity instanceof MainActivity) {
            isMain = true;
        } else if (secondActivity instanceof MainActivity && activity instanceof ContentNewDetailActivity) {
            isMain = true;
            runActivity = secondActivity;
        }
        for (MomentsDataBean momentsDataBean : list) {
            momentsDataBean.imgList = VideoAndFileUtils.getImgList(momentsDataBean.getContenturllist(),
                    momentsDataBean.getContenttext());
            momentsDataBean.isMySelf = MySpUtils.isMe(momentsDataBean.getContentuid());

            momentsDataBean.contentParseText = LikeAndUnlikeUtil.isLiked(momentsDataBean.getIsshowtitle()) ?
                    ParserUtils.htmlToJustAtText(momentsDataBean.getContenttitle()) : null;
            momentsDataBean.isShowCheckAll = calculateShowCheckAllText(momentsDataBean.contentParseText);
            AuthBean auth = momentsDataBean.getAuth();
            if (auth != null) {
                momentsDataBean.authPic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
            momentsDataBean.adView = isMain && AppUtil.isAdType(momentsDataBean.getContenttype())
                    ? ((MainActivity) runActivity).getAdView() : null;

        }
        return list;
    }

    public static MomentsDataBean getContentNewBean(MomentsDataBean bean) {
        if (bean == null) return null;
        bean.imgList = VideoAndFileUtils.getImgList(bean.getContenturllist(),
                bean.getContenttext());
        bean.isMySelf = MySpUtils.isMe(bean.getContentuid());
        //直接数据层做好是否展示的判断即可
        bean.contentParseText = LikeAndUnlikeUtil.isLiked(bean.getIsshowtitle()) ?
                ParserUtils.htmlToJustAtText(bean.getContenttitle()) : null;
        bean.isShowCheckAll = calculateShowCheckAllText(bean.contentParseText);
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
//        Log.i("maxText", "textWidth: " + textWidth + "----maxContentViewWidth:" + maxContentViewWidth
//                + "------maxLines:" + maxLines);
        return maxLines > 8.01;
    }

    /**
     * 用户话题对象转换,目前用于话题详情页使用
     * @param infoBean
     * @return
     */
    public static TopicItemBean changeTopic(TopicInfoBean infoBean) {
        if (infoBean == null) {
            return null;
        }
        TopicItemBean topicItemBean = new TopicItemBean();
        topicItemBean.tagalias = infoBean.getTagalias();
        topicItemBean.tagid = infoBean.getTagid();
        topicItemBean.tagimg = infoBean.getTagimg();
        topicItemBean.activecount = infoBean.activecount;
        return topicItemBean;
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
     * @return
     */
    public static List<UserBean> changeFocusUserToAtUser(List<UserFocusBean.RowsBean> list) {
        if (list == null || list.isEmpty())
            return null;
        ArrayList<UserBean> beanArrayList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            UserFocusBean.RowsBean rowsBean = list.get(i);
            UserBean bean = new UserBean();
            AuthBean auth = rowsBean.auth;
            if (auth != null) {
                bean.authpic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
            bean.isMe = MySpUtils.isMe(rowsBean.userid);
            bean.userid = rowsBean.userid;
            bean.username = rowsBean.username;
            bean.userheadphoto = rowsBean.userheadphoto;
            bean.groupId = "我关注的人";
            bean.uno = rowsBean.uno;
            bean.authname = rowsBean.authname;
            bean.isFocus = TextUtils.equals("1", rowsBean.isfollow);
            beanArrayList.add(bean);

        }
        return beanArrayList;
    }



    public static List<UserBean> changeSearchUser(List<UserBaseInfoBean.UserInfoBean> list) {
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
            bean.isMe = MySpUtils.isMe(rowsBean.getUserid());
            bean.isFocus = TextUtils.equals("1", rowsBean.getIsfollow());
            bean.userid = rowsBean.getUserid();
            bean.username = rowsBean.getUsername();
            bean.userheadphoto = rowsBean.getUserheadphoto();
            bean.guajianurl = rowsBean.getGuajianurl();
            bean.uno = rowsBean.getUno();
            bean.authname = rowsBean.getUsersign();
            beanArrayList.add(bean);

        }
        return beanArrayList;
    }

    /**
     * 我的关注列表项数据转换,包括话题和用户,全部统一用{@link UserBean}
     * 注意这个isMe参数是外面传的,不是根据userID每一条比较
     *
     * @param initialData
     * @param isMe
     * @param isTheme
     * @return
     */
    public static List<UserBean> getMyFocusDataBean(List<UserFocusBean.RowsBean> initialData, boolean isMe, boolean isTheme) {
        List<UserBean> resultData = new ArrayList<>();
        for (UserFocusBean.RowsBean row : initialData) {
            UserBean bean = new UserBean();
            boolean isfocus;
            //如果是专栏,直接判断,如果是跟用户相关则需要考虑两个字段
            if (isTheme) {
                isfocus = "1".equals(row.isfollow);
            } else {
                if (isMe) {
                    isfocus = "1".equals(row.eachotherflag);
                } else {
                    if ("1".equals(row.eachotherflag)) {
                        isfocus = true;
                    } else {
                        isfocus = "1".equals(row.isfollow);
                    }
                }
            }
            bean.isFocus = isfocus;
            bean.isMe = isMe;
            if (isTheme) {
                bean.userheadphoto = row.tagimg;
                bean.username = row.tagalias;
                bean.userid = row.tagid;
                bean.groupId = row.taglead;
            } else {
                bean.userheadphoto = row.userheadphoto;
                bean.username = row.username;
                bean.userid = row.userid;
                bean.groupId = row.usersign;
            }
            AuthBean auth = row.auth;
            if (auth != null) {
                bean.authpic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
            bean.authname = row.authname;
            resultData.add(bean);
        }
        return resultData;
    }


    /**
     * 粉丝列表项数据转换(他人页面的粉丝和我个人页面的粉丝逻辑不一样)
     */
    public static List<UserBean> getMyFansDataBean(List<UserFocusBean.RowsBean> initialData, boolean isMe, boolean isNeedBreak) {
        List<UserBean> resultData = new ArrayList<>(initialData.size());
        for (int i = 0; i < initialData.size(); i++) {
            //数据裁剪,为了隐藏一些马甲账号显示
            if (isNeedBreak) {
                if (i == 10) break;
            }
            UserFocusBean.RowsBean row = initialData.get(i);
            UserBean bean = new UserBean();
            boolean isfocus;
            if ("1".equals(row.eachotherflag)) {
                isfocus = true;
            } else {
                isfocus = !"0".equals(row.isfollow);
            }
            bean.isFocus = isfocus;
            bean.isMe = isMe;

            bean.userheadphoto = row.userheadphoto;
            bean.username = row.username;
            bean.userid = row.userid;
            bean.groupId = row.usersign;
            AuthBean auth = row.auth;
            if (auth != null) {
                bean.authpic = VideoAndFileUtils.getCover(auth.getAuthpic());
            }
            bean.authname = row.authname;
            resultData.add(bean);
        }
        return resultData;
    }

    /**
     * 修改通知相关的bean对象转换
     *
     * @param rows
     * @return
     */
    public static List<MessageDataBean.RowsBean> changeMsgBean(List<MessageDataBean.RowsBean> rows) {
        if (!AppUtil.listHasDate(rows)) return rows;
        for (MessageDataBean.RowsBean row : rows) {
            String timeText = "";
            try {
                Date start = DateUtils.getDate(row.createtime, DateUtils.YMDHMS);
                timeText = DateUtils.showTimeText(start);
            } catch (Exception e) {
                e.printStackTrace();
            }
            row.timeText = timeText;
//            row.noticeText = ParserUtils.htmlToJustAtText(row.notetext);
            if (row.auth != null) {
                row.authPic = VideoAndFileUtils.getCover(row.auth.getAuthpic());
            }
        }
        return rows;
    }
}
