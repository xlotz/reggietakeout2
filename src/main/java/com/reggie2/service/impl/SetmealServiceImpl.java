package com.reggie2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie2.common.CustomException;
import com.reggie2.dto.SetmealDto;
import com.reggie2.entity.Setmeal;
import com.reggie2.entity.SetmealDish;
import com.reggie2.mapper.SetmealMapper;
import com.reggie2.service.SetmealDishService;
import com.reggie2.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时插入套餐表、套餐和菜品关联表，注意添加事务
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 插入套餐表信息
        this.save(setmealDto);

        // 获取套餐和菜品关联信息，并拼接字段 在每条记录上补充套餐ID
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        // 保存套餐和菜品关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 通过套餐ID获取套餐信息以及菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 查询套餐信息
        Setmeal setmeal = this.getById(id);
        // 构建新对象用于存放套餐及菜品信息
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 构建查询构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        // 构建过滤条件
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 修改套餐信息和关联度的菜品信息 setmeal, setmeal_dish
     * 对setmeal_dish 的操作为先删除，后添加
     * 注意添加事务
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {

        // 定义一个新对象用于保存关联的菜品信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        // 修改套餐信息
        log.info("更新的套餐信息: {}", setmeal);
        this.updateById(setmeal);

        // 删除套餐关联的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        setmealDishService.remove(queryWrapper);
        // 拼接要保存的关联菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().peek((item) -> {
//            log.info("获取套餐id: {}", setmealDto.getId());
            item.setSetmealId(setmeal.getId());
        }).collect(Collectors.toList());

        log.info("更新后的菜品信息: {}", setmealDishes.toArray());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 修改套餐状态，包含单条和批量
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        log.info("获取到的ids: {}, 要修改的状态: {}", ids, status);

        if (ids.size()<=0){
            throw new CustomException("要修改的套餐ID为空");
        }
        ids.forEach(item->{
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId, item);
            Setmeal setmeal = new Setmeal();
            setmeal.setStatus(status);
            this.update(setmeal, queryWrapper);
        });
    }

    /**
     * 删除套餐以及关联的菜品，需要操作 setmeal 和 setmeal_dish 注意事务
     * 需要判断套餐是否正则售卖
     * @param ids
     */
    @Override
    @Transactional
    public void removeByIdWithDish(List<Long> ids) {
        log.info("获取的ids列表: {}", ids);
        if (ids.size() <=0){
            throw new CustomException("要删除的套餐ID列表为空");
        }
        // 查询套餐是否可删除
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        setmealQueryWrapper.in(Setmeal::getId, ids);
        setmealQueryWrapper.eq(Setmeal::getStatus, 1);

        log.info("count: {}", this.count(setmealQueryWrapper));
        // 统计符合条件的套餐
        if (this.count(setmealQueryWrapper) >0){
            throw new CustomException("该套餐正在售卖，不能删除");
        }

        // 删除套餐
        this.removeBatchByIds(ids);

        // 根据套餐获取对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);
    }


}
