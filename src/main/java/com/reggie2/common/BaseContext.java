package com.reggie2.common;

/**
 *  * 基于ThreadLocal 封装工具类，用于保存和获取当前登录用户ID
 *  * 作用范围是某一个线程内
 * @author
 * @date 2023/8/8
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
