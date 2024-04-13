package com.javamall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.javamall.entity.*;
import com.javamall.service.IOrderDetailService;
import com.javamall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端-订单Controller控制器
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    /**
     * 根据条件分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean){
        //查询条件
        Map<String,Object> query=new HashMap<>();
        query.put("orderNo",pageBean.getQuery().trim());
        query.put("start",(pageBean.getPageNum()-1)*pageBean.getPageSize());
        query.put("pageSize",pageBean.getPageSize());

        //查询结果
        List<Order> list = orderService.list(query);
        Long total = orderService.getTotal(query);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("orderList",list);
        resultMap.put("total",total);

        return R.ok(resultMap);
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/updateStatus")
    public R updateStatus(@RequestBody Order order){
        Order resultOrder = orderService.getById(order.getId());
        resultOrder.setStatus(order.getStatus());
        orderService.saveOrUpdate(resultOrder);
        return R.ok();
    }

    /**
     * 删除
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id){
        // 删除订单细表的数据
        orderDetailService.remove(new QueryWrapper<OrderDetail>().eq("mId",id));
        orderService.removeById(id);
        return R.ok();
    }

}
