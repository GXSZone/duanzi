package com.caotu.duanzhi.Http;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmBean extends RealmObject {
    @PrimaryKey
    private String contentId;
    private long time;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
