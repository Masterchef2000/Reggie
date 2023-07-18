package com.cello.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cello.reggie.common.CustomException;
import com.cello.reggie.common.R;
import com.cello.reggie.dto.SetmealDto;
import com.cello.reggie.entity.Dish;
import com.cello.reggie.entity.Setmeal;
import com.cello.reggie.entity.SetmealDish;
import com.cello.reggie.mapper.SetmealMapper;
import com.cello.reggie.service.SetmealDishService;
import com.cello.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐：1.将套餐保存到setmeal表中 2.将套餐中的菜保存到setmealdish表中
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐到setmeal表中
        save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //为每一道菜附上对应的套餐id
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存每道菜到setmealdish表中
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 获取套餐详情
     * @param Id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long Id) {
        Setmeal setmeal = super.getById(Id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        List<SetmealDish> list = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void removeWithDish(List<Long> ids) {

        //查询要删除的菜品是否在售，是的话则拒绝删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);
        int cnt = super.count(wrapper);
        if(cnt > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //删除套餐
        super.removeByIds(ids);

        //删除套餐中的菜品
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper= new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    /**
     * 【未完成】修改套餐状态（在售/停售）
     * @param ids
     * @param tosell
     */
    @Override
    public void changeStatusWithDish(List<Long> ids, boolean tosell) {

        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        if(!tosell){
            updateWrapper.eq("status","0");
            updateWrapper.set("status","1");
        }else {
            updateWrapper.eq("status","1");
            updateWrapper.set("status","0");
        }



    }


}
