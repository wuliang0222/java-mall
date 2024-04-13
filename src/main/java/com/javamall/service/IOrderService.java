package com.javamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.javamall.entity.Order;
import java.util.List;
import java.util.Map;


/**
 * 订单主表Service接口
 */
public interface IOrderService extends IService<Order> {

    /**
     * 根据条件 分页查询订单
     */
     List<Order> list(Map<String,Object> map);

    /**
     * 根据条件，查询订单总记录数
     */
     Long getTotal(Map<String,Object> map);

    /**
     * 创建订单
     */
    Order createOrder(Order order, String openId);

    /**
     * 检查库存
     */
    boolean checkStock(Order order);
}
