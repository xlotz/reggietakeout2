package com.reggie2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie2.entity.Category;

/**
 * @author
 * @date 2023/8/9
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
