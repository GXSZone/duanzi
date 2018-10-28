package com.caotu.duanzhi.Http.bean;

import java.io.Serializable;

/**
 * 针对只有code和message的情况
 */
public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public String code;
    public String msg;

    public BaseResponseBean toLzyResponse() {
        BaseResponseBean bean = new BaseResponseBean();
        bean.setCode(code);
        bean.setMessage(msg);
        return bean;
    }
}

