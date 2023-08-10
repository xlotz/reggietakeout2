package com.reggie2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie2.dto.SetmealDto;
import com.reggie2.entity.Setmeal;

import java.util.List;

/**
 * @author
 * @date 2023/8/9
 */
public interface SetmealService extends IService<Setmeal> {

    // 扩展方法， 通过套餐接口，保存套餐信息和套餐以及菜品关联信息
    public void saveWithDish(SetmealDto setmealDto);

    // 扩展方法，通过套餐ID，获取套餐信息和套餐管理的菜品信息
    public SetmealDto getByIdWithDish(Long id);

    // 扩展方法，修改套餐信息以及关联的菜品信息
    public void updateWithDish(SetmealDto setmealDto);

    // 扩展方法，修改套餐状态
    public void updateStatus(List<Long> ids, Integer status);

    // 扩展方法，通过套餐ID 删除套餐以及关联的菜品信息
    public void removeByIdWithDish(List<Long> ids);
}
