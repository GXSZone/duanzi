package weige.umenglib;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@IntDef({
        ThirdPlatform.weixin,
        ThirdPlatform.wei_pyq,
        ThirdPlatform.qq,
        ThirdPlatform.qqzone,
        ThirdPlatform.sina
})
public @interface ThirdPlatform {
    int weixin = 10;
    int wei_pyq = 11;
    int qq = 12;
    int qqzone = 13;
    int sina = 14;
}