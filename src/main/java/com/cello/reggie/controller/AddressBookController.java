package com.cello.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cello.reggie.common.BaseContext;
import com.cello.reggie.common.R;
import com.cello.reggie.entity.AddressBook;
import com.cello.reggie.service.AddressBookService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook={}",addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook={}",addressBook);

        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    @PutMapping("/default")
    public R<AddressBook> getDefault(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());

        //条件构造器
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        updateWrapper.set(AddressBook::getIsDefault,0);

        //将与用户id所关联的所有地址的is_default字段更新为0
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        //再将前端传递的地址id的is_default字段更新为1
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 获取用户默认Id
     * @return
     */
    @GetMapping("/default")
    R<AddressBook> getDefault(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,currentId);
        wrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(wrapper);
        return R.success(addressBook);
    }
}
