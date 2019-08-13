package weige.umenglib;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class AuthCallBack implements UMAuthListener {
    @Override
    public void onStart(SHARE_MEDIA share_media) {
        start();
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        UserAuthInfo umInfoBean = new UserAuthInfo();
        if (map != null) {
            // TODO: 2019-08-13 QQ 这个有点特殊,为了跟IOS 的账号互通
            if (share_media == SHARE_MEDIA.QQ) {
                umInfoBean.setUid(map.get("unionid"));
            } else {
                umInfoBean.setUid(map.get("uid"));
            }
            umInfoBean.setName(map.get("name"));
            umInfoBean.setIconurl(map.get("iconurl"));
            umInfoBean.setGender(map.get("gender"));
        }
        complete(SharePlatformTranlate.umengTranlateToUser(share_media), umInfoBean);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        error(throwable == null ? "" : throwable.getMessage());

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        cancle();
    }

    public void error(String s) {

    }

    protected void cancle() {

    }

    public void complete(@ThirdPlatform int platform, UserAuthInfo umInfoBean) {

    }

    public void start() {

    }
}
