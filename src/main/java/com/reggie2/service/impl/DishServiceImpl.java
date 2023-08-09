package com.reggie2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie2.dto.DishDto;
import com.reggie2.entity.Dish;
import com.reggie2.entity.DishFlavor;
import com.reggie2.mapper.DishMapper;
import com.reggie2.service.DishFlavorService;
import com.reggie2.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @date 2023/8/9
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 添加菜品信息，同时修改 dish , dish_flavor 表， 注意添加事务
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品信息
        this.save(dishDto);

        // 获取菜品ID
        Long dishId = dishDto.getId();
        // 通过菜品id 获取菜品口味, 拼接 菜品口味 和菜品ID 的关联关系
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item->{
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());

        log.info("菜品口味和菜品ID关联信息: {}", flavors);
        // 保存口味信息到 dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 通过菜品ID，获取菜品信息和菜品口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        return null;
    }
}
