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
}
