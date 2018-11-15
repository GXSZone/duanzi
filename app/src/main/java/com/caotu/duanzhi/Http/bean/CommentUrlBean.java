package com.caotu.duanzhi.Http.bean;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe 评论下面的视频和图片展示
 */
public class CommentUrlBean {

    /**
     * cover : 资源封面URL
     * type : 1横视频2竖视频3图片4GIF
     * info : 资源URL
     */

    public String cover;
    public String type;
    public String info;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
