package com.cello.reggie.dto;

import com.cello.reggie.entity.Dish;
import com.cello.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 与实体类不对应，使用dto接收页面提交的数据
 */
@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
