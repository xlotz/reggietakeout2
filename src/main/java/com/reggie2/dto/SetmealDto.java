package com.reggie2.dto;

import com.reggie2.entity.Setmeal;
import com.reggie2.entity.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * 套餐扩展字段
 * @author
 * @date 2023/8/10
 */
@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
