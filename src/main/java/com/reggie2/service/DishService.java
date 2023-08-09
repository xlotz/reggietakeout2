package com.reggie2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie2.dto.DishDto;
import com.reggie2.entity.Dish;

/**
 * @author
 * @date 2023/8/9
 */
public interface DishService extends IService<Dish> {

    // 扩展方法，同时插入 菜品 和口味信息
    public void saveWithFlavor(DishDto dishDto);
    // 扩展方法，通过菜品ID，获取菜品和菜品口味信息
    DishDto getByIdWithFlavor(Long id);
}
