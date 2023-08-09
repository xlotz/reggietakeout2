package com.reggie2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie2.common.Result;
import com.reggie2.dto.DishDto;
import com.reggie2.entity.Category;
import com.reggie2.entity.Dish;
import com.reggie2.service.CategoryService;
import com.reggie2.service.DishFlavorService;
import com.reggie2.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @date 2023/8/9
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品和口味信息，注意这里重定义保存方法，操作两张表 dish, dish_flavor 表，需要添加事务
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        log.info("DishDto : {}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return Result.success("添加菜品和口味信息成功");
    }

    /**
     * 分页查询菜品信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        // 构建分页构造器
        Page<Dish> dishPageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        // 构建查询构造
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(dishPageInfo, queryWrapper);

        // 重新构造返回字段
        BeanUtils.copyProperties(dishPageInfo, dishDtoPage, "records");

        List<Dish> records = dishPageInfo.getRecords();
        List<DishDto> list = records.stream().map((item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 获取分类ID
            Long categoryId = item.getCategoryId();
            // 根据分类ID，获取分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        })).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        log.info("重构字段: {}", dishDtoPage);

        return Result.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public Result<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }
}
