package com.cello.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cello.reggie.mapper.OrderDetailMapper;
import com.cello.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;
import com.cello.reggie.entity.OrderDetail;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}

