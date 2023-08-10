package com.reggie2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie2.common.CustomException;
import com.reggie2.dto.DishDto;
import com.reggie2.entity.Dish;
import com.reggie2.entity.DishFlavor;
import com.reggie2.mapper.DishMapper;
import com.reggie2.service.DishFlavorService;
import com.reggie2.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
        // 获取菜品信息
        log.info("dish id:{}", id);
        Dish dish = this.getById(id);
        log.info("dish:{}", dish);
        // 定义一个新的对象，用于构建包含口味信息的对象
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //通过菜品ID 获取菜品分类信息

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        log.info("dish service impl dishdto: {}", dishDto);
        return dishDto;
    }

    /**
     * 更加菜品ID，更新菜品信息和口味信息，这里更新两张表，注意增加事务
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品信息
        this.updateById(dishDto);
        // 查询口味信息并删除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 插入新的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        log.info("新的口味信息: {}", flavors);
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 修改菜品状态
     * @param ids
     * @param status 获取的值即为要修改的值
     */
    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        if (ids.size()<=0){
            throw new CustomException("要修改的菜品ids为空");
        }
        ids.forEach(item->{
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId, item);
            Dish dish = new Dish();
            dish.setStatus(status);
            this.update(dish, queryWrapper);
        });

    }

    /**
     * 根据菜单ID，删除菜单和口味信息
     * 需要判断当前菜品是否为停售
     * 涉及 dish, dish_flavor 表，注意添加事务
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIdWithFlavorAndSetmeal(List<Long> ids) {
        if (ids.size()<=0){
            throw new CustomException("要删除的菜品id为空");
        }
        // 构建查询构造器
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        // 增加查询条件
        dishQueryWrapper.in(Dish::getId, ids);
        // 增加查询条件, 状态为1
        dishQueryWrapper.eq(Dish::getStatus, 1);
        log.info("统计包含提交的要删除菜品ID以及菜品状态为售卖: {}", this.count(dishQueryWrapper));
        if (this.count(dishQueryWrapper)>0){
            throw new CustomException("该菜品还在售卖，不能删除");
        }

        // 删除菜品信息
        this.removeBatchByIds(ids);

        // 通过菜品id 获取口味信息, 并删除
        LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
        flavorQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(flavorQueryWrapper);

    }
}
