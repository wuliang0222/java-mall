package com.javamall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.*;
import com.javamall.properties.WeixinProperties;
import com.javamall.service.IOrderDetailService;
import com.javamall.service.IOrderService;
import com.javamall.service.IProductService;
import com.javamall.util.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.*;

/**
 * 订单Controller控制器
 */
@RestController
@RequestMapping("/my/order/")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IProductService productService;

    /**
     * 创建订单，返回订单号
     *
     * @return
     */
    @RequestMapping("/create")
    public R create(@RequestBody Order order, @RequestHeader(value = "token") String token) {
        System.out.println("创建订单");
        Claims claims;
        // 判断token是否为空
        if (StringUtil.isNotEmpty(token)) {
            // 判断token是否失效
            claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            }
        } else {
            return R.error(500, "无权限访问！");
        }

        // 检查库存
        OrderDetail[] goods = order.getGoods();
        for (int i = 0; i < goods.length; i++) {
            OrderDetail orderDetail = goods[i];
            // 获取订单商品的购买数量
            int quantity = orderDetail.getGoodsNumber();
            // 获取商品的库存数量
            Product product = productService.getById(orderDetail.getGoodsId());
            Integer stock = product.getStock();
            // 检查购买数量是否大于库存
            if (quantity > stock) {
                // 如果购买数量大于库存，则返回错误信息
                return R.error("商品库存不足");
            }
        }

        // 创建订单信息
        String openId = claims.getId();
        order.setUserId(openId);
        order.setOrderNo("order" + DateUtil.getCurrentDateStr());
        order.setCreateDate(new Date());
        // 要先创建订单 订单详情才有主键
        orderService.saveOrUpdate(order);

        // 如果支付了 设置支付时间 扣除剩余库存
        if (order.getStatus() == 2) {
            order.setPayDate(new Date());

            for (int i = 0; i < goods.length; i++) {
                OrderDetail orderDetail = goods[i];
                // 购买数量
                int quantity = orderDetail.getGoodsNumber();
                // 库存
                Product product = productService.getById(orderDetail.getGoodsId());
                Integer stock = product.getStock();

                // 计算剩余库存
                int remainingStock = stock - quantity;
                // 更新商品的库存信息
                product.setStock(remainingStock);
                System.out.println("product:"+product.getStock());
                productService.saveOrUpdate(product);
                orderDetail.setMId(order.getId());
                orderDetailService.save(orderDetail);
            }
        }
        orderService.saveOrUpdate(order);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("orderNo", order.getOrderNo());
        return R.ok(resultMap);
    }

    /**
     * 订单查询  type 值 0 全部订单  1 待付款   2 待收货  3 退款/售后
     */
    @RequestMapping("/list")
    public R list(Integer type, Integer page, Integer pageSize, @RequestHeader(value = "token") String token) {
        String openId;
        if (StringUtil.isNotEmpty(token)) {
            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            } else {
                openId = claims.getId();
                System.out.println(openId);
            }
        } else {
            return R.error(500, "无权限访问！");
        }

        List<Order> orderList;
        Page<Order> pageOrder = new Page<>(page, pageSize);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Page<Order> orderResult;
        if (type == 0) {  // 查询全部
            orderResult = orderService.page(pageOrder, new QueryWrapper<Order>().eq("userId", openId).orderByDesc("orderNo"));
        } else {  // 根据状态查询
            orderResult = orderService.page(pageOrder, new QueryWrapper<Order>().eq("userId", openId).eq("status", type).orderByDesc("orderNo"));
        }
        resultMap.put("total", orderResult.getTotal());
        resultMap.put("totalPage", +orderResult.getPages());
        orderList = orderResult.getRecords();
        resultMap.put("page", page);
        resultMap.put("orderList", orderList);
        return R.ok(resultMap);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public R delete(Integer id) {
        orderDetailService.remove(new QueryWrapper<OrderDetail>().eq("mId", id));
        orderService.removeById(id);
        return R.ok();
    }


}
