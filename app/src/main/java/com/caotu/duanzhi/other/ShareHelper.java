package com.caotu.duanzhi.other;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.utils.MySpUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.net.URLEncoder;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe 分享三步骤:1.分享弹窗的按钮显示状态 2.分享弹窗的平台回调  3.设置数据,真正唤起三方分享平台
 * 1.createWebBean  2.getShareBeanByDetail  3.shareWeb  ---->方法的调用顺序
 */
public class ShareHelper {
    private static final ShareHelper ourInstance = new ShareHelper();

    public static ShareHelper getInstance() {
        return ourInstance;
    }

    private ShareHelper() {
    }

    /**
     * 用于同一生成一个对象,好在分享的弹窗里能展示UI,如果是视频还得提供视频的下载链接,另外收藏还得直接提供内容ID
     * 如果不是视频就不传视频url,如果不需要收藏按钮也不用传contentID,收藏直接在dialog内部处理了
     *
     * @param isVideo
     * @param HasColloection
     * @return
     */
    public WebShareBean createWebBean(boolean isVideo, boolean HasColloection,
                                      String allreadyCollection, String videoUrl, String contentId) {
        WebShareBean webShareBean = new WebShareBean();
        webShareBean.isNeedShowCollection = HasColloection;
        //1收藏 0没有  是否收藏
        webShareBean.hasColloection = TextUtils.equals("1", allreadyCollection);
        webShareBean.isVideo = isVideo;
        webShareBean.VideoUrl = videoUrl;
        webShareBean.contentId = contentId;
        return webShareBean;
    }

    /**
     * 加了个字段,复制文字使用
     *
     * @param isVideo
     * @param allreadyCollection
     * @param videoUrl
     * @param contentId
     * @return
     */
    public WebShareBean createWebBean(boolean isVideo, String allreadyCollection,
                                      String videoUrl, String contentId, String title) {
        WebShareBean webShareBean = new WebShareBean();
        webShareBean.isNeedShowCollection = true;
        //1收藏 0没有  是否收藏
        webShareBean.hasColloection = TextUtils.equals("1", allreadyCollection);
        webShareBean.isVideo = isVideo;
        webShareBean.VideoUrl = videoUrl;
        webShareBean.contentId = contentId;
        webShareBean.copyText = title;
        return webShareBean;
    }

    /**
     * 是否收藏在内部处理,根据bean对象判断,外部只需要传是否要收藏按钮即可
     * 内容详情视频播放完成后的分享
     *
     * @param item
     * @param shareMedia
     */
    public WebShareBean changeContentBean(MomentsDataBean item, SHARE_MEDIA shareMedia,
                                          String cover, String url) {
        //这个对象是新的,不是外部传的,只用于视频播放完的分享
        WebShareBean bean = new WebShareBean();
        String contenttitle = item.getContenttitle();
        if (TextUtils.equals("0", item.getIsshowtitle())) {
            contenttitle = MySpUtils.getMyName();
            if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
                contenttitle = contenttitle.substring(0, 8);
            }
            contenttitle = "来自段友" + contenttitle + "的分享";
        } else {
            if (TextUtils.isEmpty(contenttitle)) {
                contenttitle = MySpUtils.getMyName();
                if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
                    contenttitle = contenttitle.substring(0, 8);
                }
                contenttitle = "来自段友" + contenttitle + "的分享";
            }
        }
        bean.title = contenttitle;
        bean.content = BaseConfig.SHARE_CONTENT_TEXT;
        bean.icon = cover;
        if (shareMedia != null) {
            bean.medial = shareMedia;
        }
        bean.url = url;
        bean.contentId = item.getContentid();
        bean.hasColloection = "1".equals(item.getIscollection());
        bean.contentOrComment = 0;
        return bean;
    }

    /**
     * 评论详情里的视频播放完成后的分享
     *
     * @param item
     * @param cover
     * @param shareMedia
     * @param url
     * @return
     */
    public WebShareBean changeCommentBean(CommendItemBean.RowsBean item, String cover, SHARE_MEDIA shareMedia, String url) {
        //这个对象是新的,不是外部传的,只用于视频播放完的分享
        WebShareBean bean = new WebShareBean();
        String contenttitle = MySpUtils.getMyName();
        if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
            contenttitle = contenttitle.substring(0, 8);
        }
        contenttitle = "来自段友" + contenttitle + "的分享";
        bean.title = contenttitle;
        bean.content = BaseConfig.SHARE_CONTENT_TEXT;
        bean.icon = cover;
        if (shareMedia != null) {
            bean.medial = shareMedia;
        }
        bean.url = url;
        bean.contentId = item.commentid;
        bean.contentOrComment = 1;
        return bean;
    }

    /**
     * 评论列表的分享
     * 像视频的下载链接在createWebBean 方法里已经赋值处理了
     *
     * @param hasBean
     * @param item
     * @param cover
     * @param url
     * @return
     */
    public WebShareBean getShareBeanByDetail(WebShareBean hasBean, CommendItemBean.RowsBean item, String cover, String url) {
        if (hasBean == null) return null;

        String contenttitle = MySpUtils.getMyName();
        if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
            contenttitle = contenttitle.substring(0, 8);
        }
        contenttitle = "来自段友" + contenttitle + "的分享";

        hasBean.title = contenttitle;
        hasBean.content = BaseConfig.SHARE_CONTENT_TEXT;
        hasBean.icon = cover;
        hasBean.url = url;
        hasBean.contentId = item.commentid;
        hasBean.contentOrComment = 1;
        return hasBean;
    }

    /**
     * @param hasBean
     * @param item
     * @param cover
     * @param url
     * @return
     */
    public WebShareBean getShareBeanByDetail(WebShareBean hasBean, MomentsDataBean item, String cover, String url) {
        if (hasBean == null || item == null) return null;
        String contenttitle = item.getContenttitle();
        if (TextUtils.equals("0", item.getIsshowtitle())) {
            contenttitle = MySpUtils.getMyName();
            if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
                contenttitle = contenttitle.substring(0, 8);
            }
            contenttitle = "来自段友" + contenttitle + "的分享";
        } else {
            if (TextUtils.isEmpty(contenttitle)) {
                contenttitle = MySpUtils.getMyName();
                if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
                    contenttitle = contenttitle.substring(0, 8);
                }
                contenttitle = "来自段友" + contenttitle + "的分享";
            }
        }
        hasBean.title = contenttitle;
        hasBean.content = BaseConfig.SHARE_CONTENT_TEXT;
        hasBean.icon = cover;
        hasBean.url = url;
        hasBean.contentId = item.getContentid();
        hasBean.contentOrComment = 0;
        return hasBean;
    }

    /**
     * 针对web类型的分享
     *
     * @param bean
     */
    public void shareWeb(WebShareBean bean) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (activity == null || bean == null) return;
        UMImage img;
        if (TextUtils.isEmpty(bean.icon)) {
            img = new UMImage(activity, R.mipmap.ic_launcher);
        } else {
            img = new UMImage(activity, bean.icon);
        }
        img.compressStyle = UMImage.CompressStyle.SCALE;
        String userName = MySpUtils.getString(MySpUtils.SP_MY_NAME);
        String userPhoto = MySpUtils.getString(MySpUtils.SP_MY_AVATAR);
        String userNum = MySpUtils.getString(MySpUtils.SP_MY_NUM);
        String param;
        //1代表评论
        if (1 == bean.contentOrComment) {
            param = "commentid=" + bean.contentId + "&userheadphoto=" + userPhoto + "&username=" +
                    userName + "&usernumber=" + userNum;
        } else {
            param = "contendid=" + bean.contentId + "&userheadphoto=" + userPhoto + "&username=" +
                    userName + "&usernumber=" + userNum;
        }

//        if (!TextUtils.isEmpty(bean.VideoUrl)) {
//            UMVideo video = new UMVideo(bean.VideoUrl);
//            video.setTitle(bean.title);//视频的标题
//            video.setThumb(new UMImage(activity,R.mipmap.ic_launcher));//视频的缩略图
//            video.setDescription(BaseConfig.SHARE_CONTENT_TEXT);//视频的描述
//            ShareAction shareAction = new ShareAction(activity);
//            if (SHARE_MEDIA.SINA == bean.medial) {
//                //这里的文本就是新浪分享的输入框的内容
//                shareAction.withText(bean.title);
//            }
//            shareAction.withMedia(video)
//                    .setPlatform(bean.medial)//传入平台
//                    .setCallback(new MyShareListener(bean.contentId, bean.contentOrComment))//回调监听器
//                    .share();
//        }else {
        UMWeb web = new UMWeb(bean.url + "?" + URLEncoder.encode(param));
        web.setTitle(bean.title);//标题
        web.setThumb(img);  //缩略图
        web.setDescription(bean.content);//描述
        ShareAction shareAction = new ShareAction(activity);
        if (SHARE_MEDIA.SINA == bean.medial) {
            //这里的文本就是新浪分享的输入框的内容
            shareAction.withText(bean.title);
        }
        shareAction.withMedia(web)
                .setPlatform(bean.medial)//传入平台
                .setCallback(new MyShareListener(bean.contentId, bean.contentOrComment))//回调监听器
                .share();
//        }
    }

    public void shareImage(WebShareBean bean, MyShareListener listener) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        //分享emoji形式 逼格不一样
        UMImage image;
        if ((bean.url.endsWith(".gif") || bean.url.endsWith(".GIF"))
                && SHARE_MEDIA.WEIXIN.equals(bean.medial)) {
            image = new UMEmoji(runningActivity, bean.url);
        } else {
            image = new UMImage(runningActivity, bean.url);
        }
        image.setThumb(image);

        new ShareAction(runningActivity)
                .setPlatform(bean.medial)
                .setCallback(listener)
                .withMedia(image)
                .share();
    }

    /**
     * 分享卡片用
     *
     * @param media
     * @param path
     */
    public void shareImage(SHARE_MEDIA media, Bitmap path) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        UMImage image = new UMImage(runningActivity, path);
        image.setThumb(image);

        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
        new ShareAction(runningActivity)
                .setPlatform(media)
                .setCallback(new MyShareListener(null, 0))
                .withMedia(image)
                .share();
    }

    /**
     * 特意用于webview的分享
     *
     * @param bean
     */
    public void shareFromWebView(WebShareBean bean) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (activity == null || bean == null) return;
        UMImage img;
        if (TextUtils.isEmpty(bean.icon)) {
            img = new UMImage(activity, R.mipmap.ic_launcher);
        } else {
            img = new UMImage(activity, bean.icon);
        }
        UMWeb web = new UMWeb(bean.url);
        web.setTitle(bean.title);//标题
        web.setThumb(img);  //缩略图
        web.setDescription(TextUtils.isEmpty(bean.content) ? BaseConfig.SHARE_CONTENT_TEXT : bean.content);//描述

        ShareAction shareAction = new ShareAction(activity);
        if (SHARE_MEDIA.SINA == bean.medial) {
            //这里的文本就是新浪分享的输入框的内容
            shareAction.withText(bean.title);
        }
        shareAction.withMedia(web)
                .setPlatform(bean.medial)//传入平台
                .setCallback(new MyShareListener(bean.contentId, bean.contentOrComment))//回调监听器
                .share();
    }

    public void shareJustBitmap(WebShareBean bean, Bitmap path) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        UMImage image = new UMImage(runningActivity, path);
        image.setThumb(image);

        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
        new ShareAction(runningActivity)
                .setPlatform(bean.medial)
                .setCallback(new MyShareListener(null, 0))
                .withMedia(image)
                .share();
    }

    public void shareWebPicture(WebShareBean bean, String imageUrl) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        UMImage image = new UMImage(runningActivity, imageUrl);
        image.setThumb(image);
        new ShareAction(runningActivity)
                .setPlatform(bean.medial)
                .setCallback(new MyShareListener(null, 0))
                .withMedia(image)
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
