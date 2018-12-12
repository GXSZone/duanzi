package com.caotu.duanzhi.config;

public interface HttpApi {
    String OKGO_TAG = "lwQiu_okGo";

    //主页
    String MAIN_RECOMMEND_CONTENT = BaseConfig.baseApi + "/push/getpagecontent.do";//推荐-内容
    String MAIN_RECOMMEND_BANNER = BaseConfig.baseApi + "/bannertag/bannerscommend.do";//推荐-banner
    String MAIN_FOCUS_SEARCH = BaseConfig.baseApi + "/push/followcontent.do";//查询关注
    //根据内容ID获取内容详情头布局的数据,下面的评论还是需要请求另外的查看评论接口
    String DETAILID = BaseConfig.baseApi + "/content/sharecontentforapp.do";
    //首页tab分栏
    String HOME_TYPE = BaseConfig.baseApi + "/push/getsubcontent.do";
    String REQUEST_SMS_VERIFY = BaseConfig.baseApi + "/logincontrol/sms.do";//短信验证请求
    String DO_SMS_VERIFY = BaseConfig.baseApi + "/logincontrol/smscheck.do";//短信验证
    String DO_REGIST = BaseConfig.baseApi + "/logincontrol/register.do";//注册
    String DO_LOGIN = BaseConfig.baseApi + "/logincontrol/loginnew.do";//登录
    String CHANGE_PASSWORD = BaseConfig.baseApi + "/logincontrol/changepsd.do";//修改密码
    String VERIFY_HAS_REGIST = BaseConfig.baseApi + "/logincontrol/checkRegister.do";//手机号码是否已经注册
    String BIND_PHONE = BaseConfig.baseApi + "/logincontrol/bindphone.do";//绑定手机

    //我的界面
    String GET_USER_BASE_INFO = BaseConfig.baseApi + "/user/getUserinfo.do";//获取用户基本信息
    String SET_USER_BASE_INFO = BaseConfig.baseApi + "/user/updateUserinfo.do";//修改用户基本信息
    String USER_WORKSHOW = BaseConfig.baseApi + "/user/contentList.do";//我的作品
    String USER_MY_LIKE = BaseConfig.baseApi + "/user/goodContentList.do";//我喜欢的作品（点赞）
    String USER_MY_COMMENT = BaseConfig.baseApi + "/user/myCommentList.do";//我的评论
    String USER_MY_FOCUS = BaseConfig.baseApi + "/user/myFollowList.do";//我的关注
    String USER_MY_FANS = BaseConfig.baseApi + "/user/myFansList.do";//我的粉丝列表
    String USER_MY_TSUKKOMI = BaseConfig.baseApi + "/user/feedback.do";//吐槽
    String WORKSHOW_DETAILS = BaseConfig.baseApi + "/user/contentinfo.do";//作品详情
    String COLLECTION = BaseConfig.baseApi + "/collection/collectContent.do"; //我收藏的作品


    //通知及推送
    String NOTICE_OF_ME = BaseConfig.baseApi + "/user/myNotificationList.do";//我的通知
    String NOTICE_SET_READED = BaseConfig.baseApi + "/user/readNote.do";//将通知设为已读
    String NOTICE_UNREADED_COUNT = BaseConfig.baseApi + "/user/unreadNoteCount.do";//未读通知数
    String MSG_ALL_READ = BaseConfig.baseApi + "/note/onekeyread.do";//一键全部
    String COLLECTION_CONTENT = BaseConfig.baseApi + "/collection/collect.do"; //收藏
    String UNCOLLECTION_CONTENT = BaseConfig.baseApi + "/collection/uncollect.do"; //取消收藏

    //推送
    String PUSH_TAG = BaseConfig.baseApi + "/logincontrol/getaliasid.do"; //获取推送别名
    String PUSH_OPEN = BaseConfig.baseApi + "/config/openPm.do"; //点击推送后回调


    //发现
    //发现页的banner
    String DISCOVER_BANNER = BaseConfig.baseApi + "/bannertag/bannersdico.do";
    //发现页的话题列表
    String DISCOVER_LIST = BaseConfig.baseApi + "/bannertag/commendTag.do";
    String DISCOVER_GET_TAG_TREE = BaseConfig.baseApi + "/bannertag/tagtree.do";//获取标签树状列表

    //互动类
    String FOCUS_FOCUS = BaseConfig.baseApi + "/note/follow.do";//关注
    String FOCUS_UNFOCUS = BaseConfig.baseApi + "/note/unfollow.do";//取消关注
    String PARISE = BaseConfig.baseApi + "/note/good.do";//点赞
    String CANCEL_PARISE = BaseConfig.baseApi + "/note/ungood.do";//取消点赞
    String UNPARISE = BaseConfig.baseApi + "/note/bad.do";//踩
    String CANCEL_UNPARISE = BaseConfig.baseApi + "/note/unbad.do";//取消踩
    String COMMENT_BACK = BaseConfig.baseApi + "/note/comment.do";//回复评论
    String COMMENT_VISIT = BaseConfig.baseApi + "/note/cmtinfo.do";//查看评论
    String COMMENT_DELETE = BaseConfig.baseApi + "/note/delcmtinfo.do"; //删除评论

    //发布
    String WORKSHOW_PUBLISH = BaseConfig.baseApi + "/content/pushcontent.do"; //发布内容
    String WORKSHOW_DELETE = BaseConfig.baseApi + "/content/delcontent.do"; //删除内容
    String WORKSHOW_VERIFY = BaseConfig.baseApi + "/content/sencheck.do"; //校验敏感内容

    //搜索
    String SEARCH_USER = BaseConfig.baseApi + "/search/searchuser.do"; //搜索用户,目前只有用户

    String SEARCH_THEME = BaseConfig.baseApi + "/search/searchtag.do"; //搜索主题
    String SEARCH_MAIN = BaseConfig.baseApi + "/search/searchall.do"; //搜索综合
    String SEARCH_CONTENT = BaseConfig.baseApi + "/search/searchcontent.do"; //搜索内容

    //主题
    String THEME_DETAILS = BaseConfig.baseApi + "/search/gettagdetail.do"; //主题详情
    String THEME_CONTENT = BaseConfig.baseApi + "/search/gettagcontent.do"; //主题内容
    String THEME_CONTENT_DETAIL = BaseConfig.baseApi + "/content/detailcontent.do";  //内容 是否关注及专栏信息

    //分享
    String GET_SHARE_URL = BaseConfig.baseApi + "/user/shareUrl.do"; //获取分享的url
    String GET_COUNT_SHARE = BaseConfig.baseApi + "/activepage/countshare.do"; //分享计数(成功以后通知服务器计数)

    //举报
    String DO_INFORM = BaseConfig.baseApi + "/report/reportContent.do"; //举报

    //版本更新
    String VERSION = BaseConfig.baseApi + "/config/getConfig.do"; //版本更新

    //视频
    String PLAY_COUNT = BaseConfig.baseApi + "/content/playcount.do"; //播放次数统计

    //不感兴趣
    String UNLIKE = BaseConfig.baseApi + "/push/unLikeContent.do";

    //闪屏图片接口
    String SPLASH = BaseConfig.baseApi + "/config/screem.do";

    //H5分享统计
    String H5_SHARE = BaseConfig.baseApi + "/activepage/countactive.do";

    //点赞人列表
    String USERLIST = BaseConfig.baseApi + "/user/zanUserList.do";

    //统计次数的接口
    String COUNTNUMBER = BaseConfig.baseApi + "/activepage/countactivebyapp.do";
    //根据评论id获取评论详情
    String COMMENT_DEATIL = BaseConfig.baseApi + "/note/sharecmtdetail.do";
}
