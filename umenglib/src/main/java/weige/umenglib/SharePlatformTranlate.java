package weige.umenglib;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 为了在外部不直接使用任何关于友盟sdk相关的api,所以才会有这个包装类
 */
public final class SharePlatformTranlate {

    public static @ThirdPlatform
    int umengTranlateToUser(SHARE_MEDIA shareMedia) {
        byte platform = ThirdPlatform.weixin;
        switch (shareMedia) {
            case QQ:
                platform = ThirdPlatform.qq;
                break;
            case WEIXIN_CIRCLE:
                platform = ThirdPlatform.wei_pyq;
                break;
            case SINA:
                platform = ThirdPlatform.sina;
                break;
            case QZONE:
                platform = ThirdPlatform.qqzone;
                break;
        }
        return platform;
    }

    public static SHARE_MEDIA userTranlateToUmeng(@ThirdPlatform int shareMedia) {
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        switch (shareMedia) {
            case ThirdPlatform.qq:
                platform = SHARE_MEDIA.QQ;
                break;
            case ThirdPlatform.wei_pyq:
                platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case ThirdPlatform.sina:
                platform = SHARE_MEDIA.SINA;
                break;
            case ThirdPlatform.qqzone:
                platform = SHARE_MEDIA.QZONE;
                break;
        }
        return platform;
    }
}
