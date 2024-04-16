package com.javamall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.*;
import com.javamall.service.IOrderDetailService;
import com.javamall.service.IOrderService;
import com.javamall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 创建订单，返回订单号
     */
    @RequestMapping("/create")
    public R create(@RequestBody Order order, @RequestHeader(value = "token") String token) {
        // 判断token
        R r = TokenUtil.checkToken(token);
        if (r.get("code").equals(500)) return r;
        String openId = r.get("msg").toString();

        // 检查库存
        if (!orderService.checkStock(order)) return R.error("商品库存不足");

        // 创建订单信息
        order = orderService.createOrder(order, openId);

        // 填写返回值
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderNo", order.getOrderNo());
        return R.ok(resultMap);
    }

    /**
     * 订单查询  0全部订单  1待付款  2待发货  3待收货  4待评价
     */
    @RequestMapping("/list")
    public R list(Integer type, Integer page, Integer pageSize, @RequestHeader(value = "token") String token) {
        // 判断token
        R r = TokenUtil.checkToken(token);
        if (r.get("code").equals(500)) return r;
        String openId = r.get("msg").toString();

        // 分页查询
        Page<Order> pageOrder = new Page<>(page, pageSize);
        Page<Order> orderResult;
        if (type == 0) {  // 查询全部
            orderResult = orderService.page(pageOrder, new QueryWrapper<Order>().eq("userId", openId).orderByDesc("orderNo"));
        } else {  // 根据状态查询
            orderResult = orderService.page(pageOrder, new QueryWrapper<Order>().eq("userId", openId).eq("status", type).orderByDesc("orderNo"));
        }

        // 填写返回值
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", orderResult.getTotal());
        resultMap.put("totalPage", +orderResult.getPages());
        resultMap.put("page", page);
        List<Order> orderList = orderResult.getRecords();
        resultMap.put("orderList", orderList);
        return R.ok(resultMap);
    }

    /**
     * 更新订单状态
     */
    @RequestMapping("/updateStatus")
    public R updateStatus(String id, Integer status, @RequestHeader(value = "token") String token) {
        // 判断token
        R r = TokenUtil.checkToken(token);
        if (r.get("code").equals(500)) return r;

        Order order = orderService.getById(id);

        if (status == 2) order.setPayDate(new Date());
        order.setStatus(status);
        orderService.saveOrUpdate(order);
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(Integer id) {
        orderDetailService.remove(new QueryWrapper<OrderDetail>().eq("mId", id));
        orderService.removeById(id);
        return R.ok();
    }


}
