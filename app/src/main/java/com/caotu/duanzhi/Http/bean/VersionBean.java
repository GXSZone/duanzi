package com.caotu.duanzhi.Http.bean;

/**

 */
public class VersionBean {

    public LasterBean newestversionandroid;

    public MustUpdateBaen updateanversiondroid;

    public static class LasterBean {
        public String value;
        public String message;
        public String linkurl;
    }

    public static class MustUpdateBaen {
        public String value;
        public String message;
        public String linkurl;
    }
}
