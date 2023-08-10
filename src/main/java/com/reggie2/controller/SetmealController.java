package com.reggie2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie2.common.Result;
import com.reggie2.dto.SetmealDto;
import com.reggie2.entity.Category;
import com.reggie2.entity.Setmeal;
import com.reggie2.entity.SetmealDish;
import com.reggie2.service.CategoryService;
import com.reggie2.service.SetmealDishService;
import com.reggie2.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @date 2023/8/10
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐信息 以及套餐和菜品关联信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        log.info("要保存的套餐相关信息: {}", setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return Result.success("保存套餐和关联菜品信息成功");
    }

    /**
     * 分页查询，获取套餐信息
     * 这里还需要获取套餐的分类信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        // 构建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        // 构建查询构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name !=null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 获取套餐分类信息
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        // 拼接 records 字段
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            // 对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            // 获取分类ID
            Long categoryId = item.getCategoryId();
            // 获取分类信息
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        log.info("获取的套餐信息: {}", setmealDtoPage.toString());
        return Result.success(setmealDtoPage);
    }

    /**
     * 通过套餐ID 获取套餐信息和关联的菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> getById(@PathVariable Long id){
        log.info("获取套餐id: {}", id);
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return Result.success(setmealDto);
    }

    /**
     * 更新套餐信息以及套餐关联的菜品信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        log.info("提交的套餐信息: {}", setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return Result.success("更新套餐信息和管理菜品信息成功");
    }

    /**
     * 修改套餐状态，包括单条和批量
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("要修改的IDS:{}, 要修改的状态: {}", ids.toArray(), status);
        setmealService.updateStatus(ids, status);
        String msg = "修改套餐状态成功";
        if (ids.size()>1){
            msg = "批量修改套餐状态成功";
        }
        return Result.success(msg);
    }

    /**
     * 根据套餐ID 删除套餐信息和关联的菜品信息
     * 需要判断套餐是否为售卖状态
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("要删除的套餐ID: {}", ids);
        setmealService.removeByIdWithDish(ids);
        return Result.success("删除套餐成功");
    }
}
