package com.cello.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cello.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
