package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.Product;
import com.javamall.mapper.ProductMapper;
import com.javamall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品Service实现类
 */
@Service("productService")
public class IProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> findSwiper() {
        return productMapper.findSwiper();
    }

    @Override
    public List<Product> findHot() {
        return productMapper.findHot();
    }

    @Override
    public List<Product> list(Map<String, Object> map) {
        return productMapper.list(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return productMapper.getTotal(map);
    }

    @Override
    public Integer add(Product product) {
        return productMapper.add(product);
    }

    @Override
    public Integer update(Product product) {
        return productMapper.update(product);
    }
}
