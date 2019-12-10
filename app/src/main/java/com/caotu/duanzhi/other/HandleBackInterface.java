package com.caotu.duanzhi.other;

/**
 * @author mac
 * @日期: 2018/11/14
 * @describe fragment处理返回事件,如果返回true 则是自己处理返回,false 则是系统默认处理
 */
public interface HandleBackInterface {

    boolean onBackPressed();
}
