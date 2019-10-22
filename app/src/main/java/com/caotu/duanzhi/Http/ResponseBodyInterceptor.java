package com.caotu.duanzhi.Http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * 处理后台数据date 字段返回不规范的问题
 * https://juejin.im/post/5dadac2ae51d4524c3745219
 */
public abstract class ResponseBodyInterceptor implements Interceptor {
    private String url;


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        url = request.url().toString();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            long contentLength = responseBody.contentLength();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();

            if ("gzip".equals(response.headers().get("Content-Encoding"))) {
                GzipSource gzippedResponseBody = new GzipSource(buffer.clone());
                buffer = new Buffer();
                buffer.writeAll(gzippedResponseBody);
            }

            MediaType contentType = responseBody.contentType();
            Charset charset;
            if (contentType == null || contentType.charset(StandardCharsets.UTF_8) == null) {
                charset = StandardCharsets.UTF_8;
            } else {
                charset = contentType.charset(StandardCharsets.UTF_8);
            }

            if (charset != null && contentLength != 0L) {
                return intercept(response, buffer.clone().readString(charset));
            }
        }
        return response;
    }

    public String getUrl() {
        return url;
    }

    abstract Response intercept(Response response, String body);


}
