package com.caotu.duanzhi.config;

public interface HttpApi {

    //主页
    String MAIN_RECOMMEND_CONTENT = BaseConfig.baseApi + "/push/getpagecontent.do";//推荐-内容

    //首页tab分栏
    String HOME_TYPE = BaseConfig.baseApi + "/push/getsubcontent.do";
    //用户登录相关接口
    String REQUEST_SMS_VERIFY = BaseConfig.baseApi + "/logincontrol/sms.do";//短信验证请求
    String DO_SMS_VERIFY = BaseConfig.baseApi + "/logincontrol/smscheck.do";//短信验证
    String DO_REGIST = BaseConfig.baseApi + "/logincontrol/register.do";//注册
    String DO_LOGIN = BaseConfig.baseApi + "/logincontrol/loginnew.do";//登录
    String CHANGE_PASSWORD = BaseConfig.baseApi + "/logincontrol/changepsd.do";//修改密码
    String VERIFY_HAS_REGIST = BaseConfig.baseApi + "/logincontrol/checkRegister.do";//手机号码是否已经注册
    String BIND_PHONE = BaseConfig.baseApi + "/logincontrol/bindphone.do";//绑定手机

    String VERIFY_LOGIN = BaseConfig.baseApi + "/logincontrol/phoneloginbysms.do";//直接验证码登录接口
    String VERIFY_LOGIN_AND_REGIST = BaseConfig.baseApi + "/logincontrol/phoneloginreg.do";//手机验证码登录注册
    String CHECK_PHONE = BaseConfig.baseApi + "/user/checkaccounttophone.do"; //设置密码 (是否可用手机号)
    String SETTING_PWD = BaseConfig.baseApi + "/user/setpsdandbindphone.do"; //设置密码(提交)

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
    String HISTORY = BaseConfig.baseApi + "/content/getcontentdetail.do"; //浏览记录
    String MINE_BANNER = BaseConfig.baseApi + "/bannertag/bannersOwn.do"; //个人中心的banner
    //通知及推送
    String NOTICE_OF_ME = BaseConfig.baseApi + "/user/myNotificationList.do";//我的通知
    String NOTICE_UNREADED_COUNT = BaseConfig.baseApi + "/user/unreadNoteCount.do";//未读通知数
    String MSG_ALL_READ = BaseConfig.baseApi + "/note/onekeyRead.do";//一键全部
    String COLLECTION_CONTENT = BaseConfig.baseApi + "/collection/collect.do"; //收藏
    String UNCOLLECTION_CONTENT = BaseConfig.baseApi + "/collection/uncollect.do"; //取消收藏
    String NOTICE_LIST = BaseConfig.baseApi + "/user/sysnoteList.do"; //新版消息列表
    String NOTICE_SETTING = BaseConfig.baseApi + "/msgSetting/editSetting.do"; //消息设置
    String NOTICE_GET_SETTING = BaseConfig.baseApi + "/msgSetting/getSetting.do"; //获取消息配置

    //推送
    String PUSH_TAG = BaseConfig.baseApi + "/logincontrol/getaliasid.do"; //获取推送别名
    String PUSH_OPEN = BaseConfig.baseApi + "/config/openPm.do"; //点击推送后回调

    //发现
    String DISCOVER_BANNER = BaseConfig.baseApi + "/bannertag/bannersdico.do";
    //发现页的话题列表
    String DISCOVER_LIST = BaseConfig.baseApi + "/bannertag/commendTag.do";
    String DISCOVER_GET_TAG_TREE = BaseConfig.baseApi + "/bannertag/tagtree.do";//获取标签树状列表
    String URL_CHECK = BaseConfig.baseApi + "/webcheck/urlManage.do"; //跳转H5url的校验
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

    //搜索
    String SEARCH_USER = BaseConfig.baseApi + "/search/searchuser.do"; //搜索用户,目前只有用户

    //主题
    String THEME_DETAILS = BaseConfig.baseApi + "/search/gettagdetail.do"; //主题详情
    String THEME_CONTENT = BaseConfig.baseApi + "/search/gettagcontent.do"; //主题内容

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

    //点赞人列表
    String USERLIST = BaseConfig.baseApi + "/user/zanUserList.do";

    //统计次数的接口
    String COUNTNUMBER = BaseConfig.baseApi + "/activepage/countactivebyapp.do";

    //根据评论id获取评论详情
    String COMMENT_DEATIL = BaseConfig.baseApi + "/note/sharecmtdetail.do";
    //青少年模式
    String TEENAGER_MODE = BaseConfig.baseApi + "/user/updateYoungModel.do";
    //上热门
    String GO_HOT = BaseConfig.baseApi + "/note/goHot.do";
}
