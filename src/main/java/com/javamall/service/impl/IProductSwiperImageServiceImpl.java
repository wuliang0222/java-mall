package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.ProductSwiperImage;
import com.javamall.mapper.ProductSwiperImageMapper;
import com.javamall.service.IProductSwiperImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品Service实现类
 */
@Service("productSwiperImageService")
public class IProductSwiperImageServiceImpl extends ServiceImpl<ProductSwiperImageMapper,ProductSwiperImage> implements IProductSwiperImageService {

    @Autowired
    private ProductSwiperImageMapper productSwiperImageMapper;


}
