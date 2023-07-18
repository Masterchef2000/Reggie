package com.cello.reggie.dto;

import com.cello.reggie.entity.Setmeal;
import com.cello.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;
    //套餐名称
    private String categoryName;
}
