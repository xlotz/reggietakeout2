package com.reggie2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie2.dto.DishDto;
import com.reggie2.entity.Dish;

import java.util.List;

/**
 * @author
 * @date 2023/8/9
 */
public interface DishService extends IService<Dish> {

    // 扩展方法，同时插入 菜品 和口味信息
    public void saveWithFlavor(DishDto dishDto);

    // 扩展方法，通过菜品ID，获取菜品和菜品口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 扩展方法，通过菜品ID， 更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);

    // 扩展方法，通过菜品ID， 更新菜品状态
    public void updateStatus(List<Long> ids, Integer status);

    // 扩展方法，通过菜品ID，删除菜品信息以及关联的口味信息和套餐信息
    public void deleteByIdWithFlavorAndSetmeal(List<Long> ids);

}
