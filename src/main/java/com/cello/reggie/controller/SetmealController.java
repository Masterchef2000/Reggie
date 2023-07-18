package com.cello.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cello.reggie.common.R;
import com.cello.reggie.dto.DishDto;
import com.cello.reggie.dto.SetmealDto;
import com.cello.reggie.entity.Category;
import com.cello.reggie.entity.Dish;
import com.cello.reggie.entity.Setmeal;
import com.cello.reggie.service.CategoryService;
import com.cello.reggie.service.DishService;
import com.cello.reggie.service.SetmealDishService;
import com.cello.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("数据传输对象setmealDto:{}",setmealDto.toString());
        setmealService.saveWithDish(setmealDto);

        return R.success("套餐保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByAsc(Setmeal::getCategoryId);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);
        //拷贝对象，然后对原对象中的records进行处理
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //处理records，给categoryname套餐名称赋值
        List<SetmealDto> list = new ArrayList<>();
        for(Setmeal item:records){
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            //根据id查找name
            Category category = categoryService.getById(categoryId);
            //判空
            if(category !=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            list.add(setmealDto);
        };

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("根据id查询菜品信息");
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);

        if(setmealDto != null){
            return R.success(setmealDto);
        }
        return R.error("没有查询到菜品");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("接收到的ids为:",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 用户前端界面中显示套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());

        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }


    @PostMapping("/status/0")
    public R<String> setNotSell(@RequestParam List<Long> ids){
        setmealService.changeStatusWithDish(ids,false);
        return R.success("更新成功！");

    }

    @PostMapping("/status/1")
    public R<String> setSell(@RequestParam List<Long> ids){
        setmealService.changeStatusWithDish(ids,true);
        return R.success("更新成功！");
    }

}
