package com.reggie2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie2.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2023/8/9
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
