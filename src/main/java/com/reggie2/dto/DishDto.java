package com.reggie2.dto;

import com.reggie2.entity.Dish;
import com.reggie2.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2023/8/9
 */
@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
