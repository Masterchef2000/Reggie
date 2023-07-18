package com.cello.reggie.controller;

import com.cello.reggie.common.R;
import com.cello.reggie.entity.OrderDetail;
import com.cello.reggie.entity.Orders;
import com.cello.reggie.service.OrderDetailService;
import com.cello.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("orders = {}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }
}
