package com.reggie2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie2.entity.DishFlavor;
import com.reggie2.mapper.DishFlavorMapper;
import com.reggie2.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2023/8/9
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
