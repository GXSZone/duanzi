package com.caotu.duanzhi.Http.tecentupload;

import android.util.Log;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.BaseConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;

/**
 * @author zhushijun QQ:775158747
 * 预签名url 链接文档:
 * https://cloud.tencent.com/document/product/436/34538
 */
public class UploadServiceTask {

    public static void upLoadFile(String cosfileTypeName, String locaFilePath, final OnUpLoadListener onUpLoadListener) {
        // 初始化 TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(MyApplication.getInstance().getCosXmlService(), transferConfig);

        String bucket = BaseConfig.COS_BUCKET_NAME;
        String uuid = java.util.UUID.randomUUID().toString();

        String cosPath = uuid + cosfileTypeName; //即对象到 COS 上的绝对路径, 格式如 cosPath = "text.txt";
        //上传对象
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, locaFilePath, null);

        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener((complete, target) -> {
            float progress = 1.0f * complete / target * 100;
//            Log.i("UploadServiceTask", String.format("progress = %d%%", (int) progress));
            onUpLoadListener.onUpLoad(progress);
        });

        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                onUpLoadListener.onLoadSuccess(result.accessUrl);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                onUpLoadListener.onLoadError("请检查您手机系统时间");
            }
        });
    }

    public interface OnUpLoadListener {
        void onUpLoad(float progress);

        void onLoadSuccess(String url);

        void onLoadError(String exception);
    }
}
