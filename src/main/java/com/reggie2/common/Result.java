package com.reggie2.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义返回值
 * @author
 * @date 2023/8/8
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Map map = new HashMap();

    public static <T> Result<T> success(T object){
        Result<T> tResult = new Result<>();
        tResult.data = object;
        tResult.code = 1;
        return tResult;
    }

    public static <T> Result<T> error(String msg){
        Result<T> tResult = new Result<>();
        tResult.msg = msg;
        tResult.code = 0;
        return tResult;
    }

    public Result<T> add(String key, Object value){
        this.map.put(key, value);
        return this;
    }
}
