package com.javamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.javamall.entity.Order;
import com.javamall.entity.R;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;


/**
 * 订单主表Service接口
 */
public interface IOrderService extends IService<Order> {

    /**
     * 根据条件 分页查询订单
     * @param map
     * @return
     */
     List<Order> list(Map<String,Object> map);

    /**
     * 根据条件，查询订单总记录数
     * @param map
     * @return
     */
     Long getTotal(Map<String,Object> map);

    /**
     * 创建订单
     * @param order
     * @param claims
     * @return
     */
    Order createOrder(Order order, Claims claims);

    /**
     * 检查库存
     * @param order
     * @return
     */
    boolean checkStock(Order order);
}
