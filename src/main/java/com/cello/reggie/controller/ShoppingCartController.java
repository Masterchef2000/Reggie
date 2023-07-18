package com.cello.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cello.reggie.common.BaseContext;
import com.cello.reggie.common.R;
import com.cello.reggie.entity.ShoppingCart;
import com.cello.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingCart = {}",shoppingCart);

        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        //查询当前菜品数量，如果已存在则数量加一，不存在则设置数量为1
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        if(dishId != null){
            //添加的是菜品
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加的是套餐
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        if(cartServiceOne != null){
            Integer num = cartServiceOne.getNumber();
            cartServiceOne.setNumber(num + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在则添加到购物车，数量为1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        if(shoppingCart.getNumber() == 1){
            shoppingCartService.removeById(shoppingCart);
            shoppingCart = new ShoppingCart();
        }
        else {
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            shoppingCartService.updateById(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        //SQL:delete from shopping_cart where user_id = ?
        shoppingCartService.remove(queryWrapper);

        return R.success("成功清空购物车");
    }

}
