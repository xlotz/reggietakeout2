package com.reggie2.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 定义元数据对象处理器
 * 处理公共字段，如创建时间、更新时间等
 * @author
 * @date 2023/8/8
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        log.info("共用字段自动填充");
    }

    /**
     * 更改操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        long pid = Thread.currentThread().getId();
        log.info("线程ID: {}", pid);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
