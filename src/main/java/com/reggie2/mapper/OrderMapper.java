package com.reggie2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie2.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2023/8/10
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
