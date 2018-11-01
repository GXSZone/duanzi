package com.caotu.duanzhi.Http;

import android.util.Log;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.BaseConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.CosXmlResult;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/10 14:40
 */
public class UploadServiceTask {
    private static final String path = "https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/";

    public static void upLoadFile(String cosfileTypeName, String locaFilePath, final OnUpLoadListener onUpLoadListener) {
        //UploadService 封装了上述分片上传请求一系列过程的类

        MyUploadService.ResumeData resumeData = new MyUploadService.ResumeData();
        //"存储桶名称"
        resumeData.bucket = BaseConfig.COS_BUCKET_NAME;
        //"[对象键](https://cloud.tencent.com/document/product/436/13324)，即存储到 COS 上的绝对路径"; //格式如 cosPath = "test.txt";
        String uuid = java.util.UUID.randomUUID().toString();
        resumeData.cosPath = uuid + cosfileTypeName;
        //"本地文件的绝对路径"; // 如 srcPath =Environment.getExternalStorageDirectory().getPath() + "/test.txt";
        resumeData.srcPath = locaFilePath;
        resumeData.sliceSize = 1024 * 1024; //每个分片的大小
        resumeData.uploadId = null; //若是续传，则uploadId不为空

        MyUploadService uploadService = new MyUploadService(MyApplication.getInstance().getCosXmlService(), resumeData);

        uploadService.setOnErrorListener(new MyUploadService.OnErrorListener() {

            @Override
            public void OnError(Exception error) {
                onUpLoadListener.onLoadError(error.getMessage());
            }
        });
        /*设置进度显示
          实现 CosXmlProgressListener.onProgress(long progress, long max)方法，
          progress 已上传的大小， max 表示文件的总大小
        */
        uploadService.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                onUpLoadListener.onUpLoad(progress, max);
            }
        });
        try {
            CosXmlResult cosXmlResult = uploadService.upload();
            onUpLoadListener.onLoadSuccess(cosXmlResult.accessUrl);
        } catch (CosXmlClientException e) {
            onUpLoadListener.onLoadError(e.getMessage());
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            String message = e.getMessage();
            Log.i("upLoadFile: ", "upLoadFile: " + message);
            onUpLoadListener.onLoadError(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            onUpLoadListener.onLoadError(e.getMessage());
        }
    }

    public interface OnUpLoadListener {
        void onUpLoad(long progress, long max);

        void onLoadSuccess(String url);

        void onLoadError(String exception);
    }
}
