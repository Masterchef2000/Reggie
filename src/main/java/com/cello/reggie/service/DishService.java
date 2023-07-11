package com.cello.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cello.reggie.dto.DishDto;
import com.cello.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应口味数据，需要操作两张表：dish dishflavor
    void saveWithFlavor(DishDto dishDto);
    void updateWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavor(Long id);
}
