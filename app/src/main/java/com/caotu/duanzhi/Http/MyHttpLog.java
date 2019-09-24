package com.caotu.duanzhi.Http;

import android.util.Log;

import com.caotu.duanzhi.config.BaseConfig;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

/**
 * 啥框架都不用,自己写log 打印拦截器,只看最重要的三要素:(关键一点是啥log框架都不用)
 * 1.请求地址;
 * 2.请求时间
 * 3.接口返回体,特别是json格式的
 */
public class MyHttpLog implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 自己加的同步是为了防止多个接口请求可以顺序打印,不错乱
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public synchronized Response intercept(Chain chain) throws IOException {
        Log.i(BaseConfig.TAG, "  \n");
        Log.i(BaseConfig.TAG, "  \n");
        log("----------------------------------------> START HTTP", false);
        Request request = chain.request();
        //打印请求体参数
        logForRequest(request, chain.connection());
        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e, false);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }


    public void log(String s, boolean isjson) {
        if (isjson) {
            formatJson(s);
        } else {
            Log.i(BaseConfig.TAG, s);
        }
    }

    private Response logForResponse(Response response, long tookMs) {

        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        log("code :" + clone.code(), false);
        try {
            log(clone.request().url() + " (" + tookMs + "ms）", false);

            if (HttpHeaders.hasBody(clone)) {
                if (responseBody == null) return response;

                byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
                MediaType contentType = responseBody.contentType();
                String body = new String(bytes, getCharset(contentType));
                log(body, true);
                responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                return response.newBuilder().body(responseBody).build();
            }

        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            log("<---------------------------------------------- END HTTP", false);
        }
        return response;
    }

    private void logForRequest(Request request, Connection connection) {

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                log(name + ": " + headers.value(i), false);
            }
        }

        if (hasRequestBody) {
            try {
                Request copy = request.newBuilder().build();
                RequestBody body = copy.body();
                if (body == null) return;
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                Charset charset = getCharset(body.contentType());
                String readString = buffer.readString(charset);
                log(readString, true);
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
        }
        log("请求参数结束--------------------------------------->", false);
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }


    /**
     * 打印接送串
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");


    public boolean formatJson(String msg) {

        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                return false;
            }
        } catch (JSONException err) {
            return false;
        }

        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.i(BaseConfig.TAG, line);
        }
        return true;
    }
}
