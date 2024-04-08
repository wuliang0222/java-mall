package com.javamall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.Order;
import com.javamall.entity.OrderDetail;
import com.javamall.entity.R;
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
     * @return
     */
    @RequestMapping("/create")
    public R create(@RequestBody Order order, @RequestHeader(value = "token") String token) {
        //判断token是否为空
        if (StringUtil.isNotEmpty(token)) {
            //判断token是否失效
            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims != null) {
                String openId = claims.getId();
                order.setUserId(openId);
                order.setOrderNo("id" + DateUtil.getCurrentDateStr());
                order.setCreateDate(new Date());
                //支付时间
                if(order.getStatus() == 2){
                    order.setPayDate(new Date());
                }
            } else {
                return R.error(500, "鉴权失败！");
            }
        } else {
            return R.error(500, "无权限访问！");
        }

        //插入数据库 订单详情
        //订单的所有商品信息
        OrderDetail[] goods = order.getGoods();
        //要先插入订单，才会有主键id
        orderService.save(order);
        for (int i = 0; i < goods.length; i++) {
            OrderDetail orderDetail = goods[i];
            orderDetail.setMId(order.getId());
            orderDetailService.save(orderDetail);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String orderNo = order.getOrderNo();
        resultMap.put("orderNo", orderNo);
        return R.ok(resultMap);
    }

    /**
     * 订单查询  type 值 0 全部订单  1 待付款   2  待收货  3 退款/退货
     */
    @RequestMapping("/list")
    public R list(Integer type, Integer page, Integer pageSize, @RequestHeader(value = "token") String token) {
        //判断token是否为空
        if (StringUtil.isNotEmpty(token)) {
            //判断token是否失效
            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims != null) {
                String openId = claims.getId();
            } else {
                return R.error(500, "鉴权失败！");
            }
        } else {
            return R.error(500, "无权限访问！");
        }

        List<Order> orderList;
        Page<Order> pageOrder = new Page<>(page, pageSize);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (type == 0) {  // 查询全部
            // orderList = orderService.list();
            Page<Order> orderReslut = orderService.page(pageOrder, new QueryWrapper<Order>().orderByDesc("orderNo"));
//            System.out.println("总记录：" + orderReslut.getTotal());
//            System.out.println("总页数：" + orderReslut.getPages());
//            System.out.println("当前页数据：" + orderReslut.getRecords());
            resultMap.put("total", orderReslut.getTotal());
            resultMap.put("totalPage", +orderReslut.getPages());
            orderList = orderReslut.getRecords();

        } else {  // 根据状态查询
            // orderList=orderService.list(new QueryWrapper<Order>().eq("status",type));
            Page<Order> orderReslut = orderService.page(pageOrder, new QueryWrapper<Order>().eq("status", type).orderByDesc("orderNo"));
//            System.out.println("总记录：" + orderReslut.getTotal());
//            System.out.println("总页数：" + orderReslut.getPages());
//            System.out.println("当前页数据：" + orderReslut.getRecords());
            resultMap.put("total", orderReslut.getTotal());
            resultMap.put("totalPage", +orderReslut.getPages());
            orderList = orderReslut.getRecords();
        }
        resultMap.put("page", page);
        resultMap.put("orderList", orderList);
        return R.ok(resultMap);
    }


}
