package com.caotu.duanzhi.module.home;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.VersionBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.view.dialog.VersionDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * @author mac
 * @日期: 2018/11/7
 * @describe TODO
 */
public class MainPresenter {

    private IMainView IView;

    public void create(IMainView iMainView) {
        this.IView = iMainView;
    }

    public void destroy() {
        IView = null;
    }

    public void requestVersion() {
        OkGo.<BaseResponseBean<VersionBean>>post(HttpApi.VERSION)
                .tag(this)
                .execute(new JsonCallback<BaseResponseBean<VersionBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<VersionBean>> response) {
                        VersionBean data = response.body().getData();
                        if (data.newestversionandroid.value.compareToIgnoreCase(
                                DevicesUtils.getVerName()) > 0) {
                            VersionDialog dialog = new VersionDialog(MyApplication.getInstance().getRunningActivity()
                                    , data);
                            dialog.show();
                        }
                    }
                });
    }
}
