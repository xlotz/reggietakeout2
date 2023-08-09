package com.reggie2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie2.common.CustomException;
import com.reggie2.entity.Category;
import com.reggie2.entity.Dish;
import com.reggie2.entity.Setmeal;
import com.reggie2.mapper.CategoryMapper;
import com.reggie2.service.CategoryService;
import com.reggie2.service.DishService;
import com.reggie2.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author
 * @date 2023/8/9
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类ID 验证菜品分类或套餐分类是否包含依赖，如果包含则提示不能删除，如果不包含可直接删除。
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 菜品分类查询构造器
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        // 查询菜品分类是否关联菜品
        long dishCount = dishService.count(dishQueryWrapper);
        if (dishCount>0){
            throw new CustomException("该菜品分类关联菜品，不能删除");
        }

        // 套餐分类查询构造器
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        long setCount = setmealService.count(setmealQueryWrapper);
        if (setCount>0){
            throw new CustomException("该套餐分类关联菜品，不能删除");
        }
        // 无关联，则直接删除
        this.removeById(id);
    }
}
