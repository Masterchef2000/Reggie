package com.cello.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cello.reggie.common.CustomException;
import com.cello.reggie.common.R;
import com.cello.reggie.entity.Category;
import com.cello.reggie.entity.Dish;
import com.cello.reggie.entity.Employee;
import com.cello.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //log.info("page = {}, pageSize = {}",page);

        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort);
        //执行查询
        dishService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @GetMapping("/{id}")
    public R<Dish> getById(@PathVariable Long id){
        log.info("根据id查询菜品信息");
        Dish dish = dishService.getById(id);
        if(dish != null){
            return R.success(dish);
        }
        return R.error("没有查询到菜品");
    }

    @PostMapping
    public R<String> add(@RequestBody Dish dish){
        try{
            dishService.save(dish);
            return R.success("新增菜品成功");
        }catch (CustomException e){
            e.printStackTrace();
        }
        return null;
    }

}
