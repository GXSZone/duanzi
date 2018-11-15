package com.caotu.duanzhi.other;

import android.app.Activity;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.sunfusheng.widget.ImageData;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe TODO
 */
public class ShareHelper {
    private static final ShareHelper ourInstance = new ShareHelper();

    public static ShareHelper getInstance() {
        return ourInstance;
    }

    private ShareHelper() {
    }

    /**
     * 封装列表分享对象,有局限性,没有包括弹窗分享
     *
     * @param item
     * @param shareMedia
     */
    public WebShareBean changeContentBean(MomentsDataBean item, SHARE_MEDIA shareMedia, String url) {
        List<ImageData> imgList = VideoAndFileUtils.getImgList(item.getContenturllist(), null);
        WebShareBean bean = new WebShareBean();
        String contenttitle = item.getContenttitle();
        if (TextUtils.isEmpty(contenttitle)) {
            contenttitle = item.getUsername();
            if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
                contenttitle.substring(0, 8);
            }
            contenttitle = "来自段友" + contenttitle + "的分享";
        }
        bean.title = contenttitle;
        bean.content = "内含段子，内含的不只是段子";
        bean.icon = imgList.get(0).url;
        bean.webType = 0;
        if (shareMedia != null) {
            bean.medial = shareMedia;
        }
        bean.url = url;
        bean.contentId = item.getContentid();
        bean.hasColloection = "1".equals(item.getIscollection());
        return bean;
    }


    public WebShareBean changeCommentBean(CommendItemBean.RowsBean item, String cover, SHARE_MEDIA shareMedia, String url) {
        WebShareBean bean = new WebShareBean();
        String contenttitle = item.commenttext;
        if (TextUtils.isEmpty(contenttitle)) {
            contenttitle = item.username;
            if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
                contenttitle.substring(0, 8);
            }
            contenttitle = "来自段友" + contenttitle + "的分享";
        }
        bean.title = contenttitle;
        bean.content = "内含段子，内含的不只是段子";
        bean.icon =cover ;
        bean.webType = 0;
        if (shareMedia != null) {
            bean.medial = shareMedia;
        }
        bean.url = url;
        bean.contentId = item.contentid;
        return bean;
    }

    /**
     * 针对web类型的分享
     *
     * @param bean
     */
    public void shareWeb(WebShareBean bean) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (activity == null || bean == null) return;
        UMImage img = new UMImage(activity, bean.icon);

        UMWeb web = new UMWeb(bean.url);
        web.setTitle(bean.title);//标题
        web.setThumb(img);  //缩略图
        web.setDescription(bean.content);//描述

        new ShareAction(activity)
                .withMedia(web)
                .setPlatform(bean.medial)//传入平台
                .setCallback(new MyShareListener(bean.contentId))//回调监听器
                .share();
    }

    /*
    UMImage image = new UMImage(ShareActivity.this, "imageurl");//网络图片
UMImage image = new UMImage(ShareActivity.this, file);//本地文件
UMImage image = new UMImage(ShareActivity.this, R.drawable.xxx);//资源文件
UMImage image = new UMImage(ShareActivity.this, bitmap);//bitmap文件
UMImage image = new UMImage(ShareActivity.this, byte[]);//字节流


UMImage thumb =  new UMImage(this, R.drawable.thumb);
image.setThumb(thumb);


image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
压缩格式设置
image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色

new ShareAction(ShareActivity.this).withText("hello").withMedia(image).share();
     */
}
