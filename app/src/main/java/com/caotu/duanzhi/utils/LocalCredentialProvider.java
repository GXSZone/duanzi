package com.caotu.duanzhi.utils;

import com.caotu.duanzhi.config.BaseConfig;
import com.tencent.cos.xml.utils.StringUtils;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.BasicQCloudCredentials;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/10 14:20
 */
public class LocalCredentialProvider extends BasicLifecycleCredentialProvider {
    private String secretKey;
    private String secretId;

    public LocalCredentialProvider(String secretId, String secretKey, long keyDuration) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        long keyDuration1 = keyDuration;
    }

    /**
     * 返回 BasicQCloudCredentials
     */
    @Override
    public QCloudLifecycleCredentials fetchNewCredentials() {
        long current = System.currentTimeMillis() / 1000L;
        long expired = current + BaseConfig.keyDuration;
        String keyTime = current + ";" + expired;
        return new BasicQCloudCredentials(secretId, secretKeyToSignKey(secretKey, keyTime), keyTime);
    }

    private String secretKeyToSignKey(String secretKey, String keyTime) {
        String signKey = null;
        try {
            if (secretKey == null) {
                throw new IllegalArgumentException("secretKey is null");
            }
            if (keyTime == null) {
                throw new IllegalArgumentException("qKeyTime is null");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            byte[] byteKey = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKey hmacKey = new SecretKeySpec(byteKey, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(hmacKey);
            signKey = StringUtils.toHexString(mac.doFinal(keyTime.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return signKey;
    }
}
