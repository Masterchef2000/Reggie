package com.cello.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cello.reggie.common.CustomException;
import com.cello.reggie.common.R;
import com.cello.reggie.dto.DishDto;
import com.cello.reggie.entity.Category;
import com.cello.reggie.entity.Dish;
import com.cello.reggie.entity.Employee;
import com.cello.reggie.service.CategoryService;
import com.cello.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //拷贝对象，然后对原对象中的records进行处理
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取原records数据
        List<Dish> records = pageInfo.getRecords();
        //处理records，给categoryname赋值
        List<DishDto> list = new ArrayList<>();
        for(Dish item:records){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id查找name
            Category category = categoryService.getById(categoryId);
            //判空
            if(category !=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            list.add(dishDto);
        };

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info("根据id查询菜品信息");
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        if(dishDto != null){
            return R.success(dishDto);
        }
        return R.error("没有查询到菜品");
    }


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("接收到的dto数据：{}",dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

}
