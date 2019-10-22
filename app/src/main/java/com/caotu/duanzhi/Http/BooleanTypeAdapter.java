package com.caotu.duanzhi.Http;

import android.text.TextUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *
 * Gson 高级使用的一些技巧:
 * https://blog.csdn.net/pngfi/article/details/62122007
 * 本类使用参考文章
 * https://blog.csdn.net/danlyalex/article/details/79963304
 * https://stackoverflow.com/questions/11399079/convert-ints-to-booleans
 * 有需求就有解决办法,解决接口返回的值不是直接能用的情况:
 * string 类型转成 Boolean 使用的情况,或者是int 类型转成boolean 使用的情况,都可以这么做
 *  注意点: bean对象里的字段还是需要跟接口一致
 *  这么使用的话就是全局了
 *    Gson gson = new GsonBuilder()
 *       .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
 *       .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
 *       .create();
 */
public class BooleanTypeAdapter extends TypeAdapter<Boolean> {

    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }

    }

    @Override
    public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return in.nextInt() == 1;
            case STRING:
                return toBoolean(in.nextString());
            default:
                return false;
//                throw new JsonParseException("Expected BOOLEAN or NUMBER but was " + peek);
        }
    }

    /**
     * true  TURE 都为true
     * "0" 为 false
     * "1" 为 true
     * @return
     */
    public static boolean toBoolean(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        } else {
            if (value.equalsIgnoreCase("true")) {
                return true;
            } else if (value.equalsIgnoreCase("false")) {
                return false;
            } else if (value.equals("1")) {
                return true;
            } else if (value.equals("0")) {
                return false;
            }
        }
        return false;
    }
}
