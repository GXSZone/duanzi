package com.caotu.duanzhi.Http;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntRange(from = 100, to = 102)
public @interface DateState {
    int init_state = 100;
    int refresh_state = 101;
    int load_more = 102;
}