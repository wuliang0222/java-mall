package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.Order;
import com.javamall.entity.OrderDetail;
import com.javamall.entity.Product;
import com.javamall.mapper.OrderMapper;
import com.javamall.service.IOrderDetailService;
import com.javamall.service.IOrderService;
import com.javamall.service.IProductService;
import com.javamall.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 订单主表Service实现类
 */
@Service("orderService")
public class IOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Override
    public List<Order> list(Map<String, Object> map) {
        return orderMapper.list(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return orderMapper.getTotal(map);
    }

    @Override
    public Order createOrder(Order order, String openId) {
        // 创建订单信息
        order.setUserId(openId);
        order.setOrderNo("order" + DateUtil.getCurrentDateStr());
        order.setCreateDate(new Date());
        // 要先创建订单 订单详情才有主键
        orderService.saveOrUpdate(order);
        // 如果支付了 设置支付时间 扣除剩余库存
        if (order.getStatus() == 2) {
            order.setPayDate(new Date());
            OrderDetail[] goods = order.getGoods();
            for (OrderDetail orderDetail : goods) {
                // 购买数量
                int quantity = orderDetail.getGoodsNumber();
                // 库存
                Product product = productService.getById(orderDetail.getGoodsId());
                Integer stock = product.getStock();
                // 计算剩余库存
                int remainingStock = stock - quantity;
                // 更新商品的库存信息
                product.setStock(remainingStock);
                productService.saveOrUpdate(product);
                orderDetail.setMId(order.getId());
                orderDetailService.save(orderDetail);
            }
        }
        orderService.saveOrUpdate(order);
        return order;
    }

    @Override
    public boolean checkStock(Order order) {
        OrderDetail[] goods = order.getGoods();
        for (OrderDetail orderDetail : goods) {
            // 获取订单商品的购买数量
            int quantity = orderDetail.getGoodsNumber();
            // 获取商品的库存数量
            Product product = productService.getById(orderDetail.getGoodsId());
            Integer stock = product.getStock();
            // 检查购买数量是否大于库存
            if (quantity > stock) {
                // 如果购买数量大于库存，则返回错误信息
                return false;
            }
        }
        return true;
    }
}
