package com.javamall.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.PageBean;
import com.javamall.entity.Product;
import com.javamall.entity.R;
import com.javamall.entity.SmallType;
import com.javamall.service.IProductService;
import com.javamall.service.ISmallTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端-商品小类Controller控制器
 */
@RestController
@RequestMapping("/admin/smallType")
public class AdminSmallTypeController {

    @Autowired
    private ISmallTypeService smallTypeService;

    @Autowired
    private IProductService productService;

    /**
     * 根据条件分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", pageBean.getQuery().trim());
        map.put("start", (pageBean.getPageNum() - 1) * pageBean.getPageSize());
        map.put("pageSize", pageBean.getPageSize());
        List<SmallType> list = smallTypeService.list(map);
        Long total = smallTypeService.getTotal(map);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("smallTypeList", list);
        resultMap.put("total", total);
        return R.ok(resultMap);
    }

    /**
     * 添加或者修改
     */
    @PostMapping("/save")
    public R save(@RequestBody SmallType smallType) {
        if (smallType.getId() == null || smallType.getId() == -1) {
            smallTypeService.add(smallType);
        } else {
            smallTypeService.update(smallType);
        }
        return R.ok();
    }


    /**
     * 删除
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id) {
        // 加个判断 大类下面如果有小类，返回报错提示
        if (productService.count(new QueryWrapper<Product>().eq("typeId", id)) > 0) {
            return R.error(500, "小类下面有商品信息，不能删除");
        } else {
            smallTypeService.removeById(id);
            return R.ok();
        }
    }

    /**
     * 根据商品大类id，查询商品小类
     */
    @RequestMapping("/listAll/{bigTypeId}")
    public R listAll(@PathVariable(value = "bigTypeId") Integer bigTypeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("smallTypeList", smallTypeService.list(new QueryWrapper<SmallType>().eq("bigTypeId", bigTypeId)));
        return R.ok(map);
    }

    /**
     * 根据商品小类id，查询对应的商品大类
     */
    @GetMapping("/getBigTypeIdBySmallTypeId/{id}")
    public R getBigTypeIdBySmallTypeId(@PathVariable(value = "id") Integer id) {
        Map<String, Object> map = new HashMap<>();
        Integer bigTypeId = smallTypeService.getById(id).getBigTypeId();
        map.put("bigTypeId", bigTypeId);
        return R.ok(map);
    }

}
