package weige.umenglib;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class ShareCallBack implements UMShareListener {
    @Override
    public void onStart(SHARE_MEDIA share_media) {
        shareStart();
    }
    @Override
    public void onResult(SHARE_MEDIA share_media) {
        shareSuccess();
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        shareError(throwable.getMessage());
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        shareCancle();
    }

    public void shareCancle() {}
    public void shareStart() {}
    public void shareError(String message) {}
    public void shareSuccess() {}
}
