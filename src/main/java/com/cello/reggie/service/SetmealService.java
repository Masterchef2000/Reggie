package com.cello.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cello.reggie.dto.SetmealDto;
import com.cello.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);
    SetmealDto getByIdWithDish(Long Id);
    void removeWithDish(List<Long> ids);
    void changeStatusWithDish(List<Long> ids, boolean tosell);
}
