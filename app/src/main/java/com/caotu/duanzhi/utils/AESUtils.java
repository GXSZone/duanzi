package com.caotu.duanzhi.utils;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONObject;

import java.security.Key;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/15 18:12
 */
public class AESUtils {

    // 密钥 长度不得小于24
    private final static String secretKey = "ctkj8888888888888888ctkj";
    // 向量 可有可无 终端后台也要约定
    private final static String iv = "01234567";
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";

    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encode(String plainText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64.encodeToString(encryptData, Base64.DEFAULT);
    }

    public static String getRequestBodyAES(Map map) {
        String aseString = null;
        try {
            JSONObject jsonObject = new JSONObject(map);
            aseString = encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aseString;
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode(String encryptText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText, Base64.DEFAULT));

        return new String(decryptData, encoding);
    }

    /**
     * 获取字符的Base64编码
     *
     * @param plainText
     * @return
     * @throws Exception
     */
    public static String getBase64(String plainText) {
        if (TextUtils.isEmpty(plainText)) return null;
        return Base64.encodeToString(plainText.getBytes(), Base64.DEFAULT);
    }

    public static String getLongBase64(String plainText) {
        if (TextUtils.isEmpty(plainText)) return null;
        return Base64.encodeToString(plainText.getBytes(), Base64.NO_WRAP);
    }
}
