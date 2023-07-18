package com.cello.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cello.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
