package com.caotu.duanzhi.other;

public class VideoDownloadHelper {
    private static final VideoDownloadHelper ourInstance = new VideoDownloadHelper();

    public static VideoDownloadHelper getInstance() {
        return ourInstance;
    }

    private VideoDownloadHelper() {
    }
    interface DownLoadCallBack{
        void success(String videoPath);
        void error(String errorMsg);
    }

    public void startVideo(String url,DownLoadCallBack listener){

    }
}
