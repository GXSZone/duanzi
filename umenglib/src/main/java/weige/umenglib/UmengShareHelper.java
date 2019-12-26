package weige.umenglib;

import android.app.Activity;
import android.graphics.Bitmap;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * 分享入口,可以把所有分享形式都写在这里,APP model里的代码还没有抽离干净,反正这么干就对了
 */
public class UmengShareHelper {


    public static void shareJustBitmap(SHARE_MEDIA platform, Activity context, Bitmap path, ShareCallBack callBack) {
        UMImage image = new UMImage(context, path);
        image.setThumb(image);
        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
        new ShareAction(context)
                .setPlatform(platform)
                .setCallback(callBack)
                .withMedia(image)
                .share();
    }

    public static void shareWebPicture(SHARE_MEDIA platform, String imageUrl, Activity context, ShareCallBack callBack) {
        UMImage image = new UMImage(context, imageUrl);
        image.setThumb(image);
        new ShareAction(context)
                .setPlatform(platform)
                .setCallback(callBack)
                .withMedia(image)
                .share();
    }

    /*

    微信小程序
目前只有微信好友支持小程序分享，朋友圈，收藏及其他平台暂不支持：
            UMMin umMin = new UMMin(Defaultcontent.url);
            //兼容低版本的网页链接
            umMin.setThumb(imagelocal);
            // 小程序消息封面图片
            umMin.setTitle(Defaultcontent.title);
            // 小程序消息title
            umMin.setDescription(Defaultcontent.text);
            // 小程序消息描述
            umMin.setPath("pages/page10007/xxxxxx");
            //小程序页面路径
            umMin.setUserName("gh_xxxxxxxxxxxx");
            // 小程序原始id,在微信平台查询
            new ShareAction(ShareDetailActivity.this)
            .withMedia(umMin)
            .setPlatform(share_media)
            .setCallback(shareListener).share();

    */
}
