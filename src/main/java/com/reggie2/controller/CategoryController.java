package com.reggie2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie2.common.Result;
import com.reggie2.entity.Category;
import com.reggie2.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author
 * @date 2023/8/9
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("新增菜品分类: {}", category);
        categoryService.save(category);
        return Result.success("新增菜品分类成功");
    }

    /**
     * 分页查询菜品分类、套餐分类
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 查询构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getUpdateTime);
        // 查询
        categoryService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);

    }

    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息: {}", category);
        categoryService.updateById(category);
        return Result.success("修改分类信息成功");
    }

    /**
     * 根据ID删除分类信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("要删除的分类ID: {}", ids);
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return Result.success("删除分类信息成功");
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件u
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 使用排序字段和更新时间排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 查询
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
