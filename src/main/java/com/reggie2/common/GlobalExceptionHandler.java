package com.reggie2.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * @author
 * @date 2023/8/9
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * SQL相关的异常错误
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if (ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            String msg = s[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error(ex.getMessage());
    }

    /**
     * 自定义业务异常
     * 数据表关联时使用
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        return Result.error(ex.getMessage());

    }
}
