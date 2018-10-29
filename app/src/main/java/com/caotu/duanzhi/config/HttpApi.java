package com.caotu.duanzhi.config;

public interface HttpApi {
    String OKGO_TAG = "lwQiu_okGo";
    //下载地址
    String DownLoadUrl = "http://sj.qq.com/myapp/detail.htm?apkName=com.caotu.toutu";

    //社区公约地址
    String Community_Convention_Url = "https://v3.toutushare.com/appbanner/pact.html";

    String REQUEST_SMS_VERIFY = BaseConfig.baseApi + "logincontrol/sms.do";//短信验证请求
    String DO_SMS_VERIFY = BaseConfig.baseApi + "logincontrol/smscheck.do";//短信验证
    String DO_REGIST = BaseConfig.baseApi + "logincontrol/register.do";//注册
    String DO_LOGIN = BaseConfig.baseApi + "logincontrol/loginnew.do";//登录
    String CHANGE_PASSWORD = BaseConfig.baseApi + "logincontrol/changepsd.do";//修改密码
    String VERIFY_HAS_REGIST = BaseConfig.baseApi + "logincontrol/checkRegister.do";//手机号码是否已经注册
    String BIND_PHONE = BaseConfig.baseApi + "logincontrol/bindphone.do";//绑定手机

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

    //通知及推送
    String NOTICE_OF_ME = BaseConfig.baseApi + "/user/myNotificationList.do";//我的通知
    String NOTICE_SET_READED = BaseConfig.baseApi + "/user/readNote.do";//将通知设为已读
    String NOTICE_UNREADED_COUNT = BaseConfig.baseApi + "/user/unreadNoteCount.do";//未读通知数
    String MSG_ALL_READ = BaseConfig.baseApi + "/note/onekeyread.do";//一键全部

    //推送
    String PUSH_TAG = BaseConfig.baseApi + "/logincontrol/getaliasid.do"; //获取推送别名
    String PUSH_OPEN = BaseConfig.baseApi + "/config/openPm.do"; //点击推送后回调

    //主页
    String MAIN_RECOMMEND_CONTENT = BaseConfig.baseApi + "/push/sharecontent.do";//推荐-内容
    String MAIN_RECOMMEND_BANNER = BaseConfig.baseApi + "/bannertag/bannerscommend.do";//推荐-banner
    String MAIN_FOCUS_SEARCH = BaseConfig.baseApi + "/push/followcontent.do";//查询关注

    //发现
    String DISCOVER_GET_THIRD_TAG = BaseConfig.baseApi + "/bannertag/smalltag.do";//获取专栏三级标签
    String DISCOVER_GET_BIG_BANNER = BaseConfig.baseApi + "/bannertag/bannersdico.do";//发现页banner、大标签
    String DISCOVER_GET_BANNER_THEME_LIST = BaseConfig.baseApi + "/bannertag/smalltag.do";//获取banner中主题合集
    String DISCOVER_GET_LITTLE_TAG = BaseConfig.baseApi + "/bannertag/commendTag.do";//获取推荐小标签列表
    String DISCOVER_GET_TAG_TREE = BaseConfig.baseApi + "/bannertag/tagtree.do";//获取标签树状列表


    //互动类
    String FOCUS_FOCUS = BaseConfig.baseApi + "/note/follow.do";//关注
    String FOCUS_UNFOCUS = BaseConfig.baseApi + "/note/unfollow.do";//取消关注
    String PARISE = BaseConfig.baseApi + "/note/good.do";//点赞
    String UNPARISE = BaseConfig.baseApi + "/note/bad.do";//踩
    String COMMENT_BACK = BaseConfig.baseApi + "/note/comment.do";//回复评论
    String COMMENT_VISIT = BaseConfig.baseApi + "/note/cmtinfo.do";//查看评论

    //发布
    String WORKSHOW_PUBLISH = BaseConfig.baseApi + "/content/pushcontent.do"; //发布内容
    String WORKSHOW_DELETE = BaseConfig.baseApi + "/content/delcontent.do"; //删除内容
    String WORKSHOW_VERIFY = BaseConfig.baseApi + "/content/sencheck.do"; //校验敏感内容

    //搜索
    String SEARCH_THEME = BaseConfig.baseApi + "/search/searchtag.do"; //搜索主题
    String SEARCH_MAIN = BaseConfig.baseApi + "/search/searchall.do"; //搜索综合
    String SEARCH_USER = BaseConfig.baseApi + "/search/searchuser.do"; //搜索用户
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
    String H5_SHARE = BaseConfig.baseApi + "activepage/countactive.do";
}
