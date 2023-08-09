package com.reggie2.common;

/**
 * 自定义业务异常
 * @author
 * @date 2023/8/9
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
