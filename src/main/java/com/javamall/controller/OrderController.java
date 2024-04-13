package com.javamall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.*;
import com.javamall.service.IOrderDetailService;
import com.javamall.service.IOrderService;
import com.javamall.util.*;
import io.jsonwebtoken.Claims;
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
        if (!orderService.checkStock(order)) {
            return R.error("商品库存不足");
        }

        // 创建订单信息
        order = orderService.createOrder(order, openId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderNo", order.getOrderNo());
        return R.ok(resultMap);
    }

    /**
     * 订单查询  type 值 0 全部订单  1 待付款   2 待收货  3 退款/售后
     */
    @RequestMapping("/list")
    public R list(Integer type, Integer page, Integer pageSize, @RequestHeader(value = "token") String token) {
        Claims claims;
        if (StringUtil.isNotEmpty(token)) {
            claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            }
        } else {
            return R.error(500, "无权限访问！");
        }

        String openId = claims.getId();
        List<Order> orderList;
        Page<Order> pageOrder = new Page<>(page, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
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
     */
    @RequestMapping("/delete")
    public R delete(Integer id) {
        orderDetailService.remove(new QueryWrapper<OrderDetail>().eq("mId", id));
        orderService.removeById(id);
        return R.ok();
    }


}
