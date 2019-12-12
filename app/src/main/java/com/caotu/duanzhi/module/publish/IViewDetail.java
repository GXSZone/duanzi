package com.caotu.duanzhi.module.publish;

/**
 * 用户详情页实现的接口
 */
public interface IViewDetail {

    /**
     * 直接拿fragment 的presenter 来交互
     * 因为内容详情和评论详情的presenter 又有些不一样
     *
     * @return
     */
    PublishPresenter getPresenter();

}
