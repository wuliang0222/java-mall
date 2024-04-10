package com.javamall.controller.admin;


import com.javamall.entity.OotdImage;
import com.javamall.entity.PageBean;
import com.javamall.entity.R;
import com.javamall.service.IOotdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端-虚拟试衣Controller控制器
 */
@RestController
@RequestMapping("/admin/ootd")
public class AdminOotdImageController {
    @Autowired
    private IOotdImageService ootdImageService;

    /**
     * 根据条件分页查询
     *
     * @param pageBean
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean) {
        System.out.println("pageBean" + pageBean);
        //查询条件
        Map<String, Object> query = new HashMap<>();
        query.put("ootdNo", pageBean.getQuery().trim());
        query.put("start", (pageBean.getPageNum() - 1) * pageBean.getPageSize());
        query.put("pageSize", pageBean.getPageSize());

        //查询结果
        List<OotdImage> list = ootdImageService.list(query);
        Long total = ootdImageService.getTotal(query);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("ootdImageList", list);
        resultMap.put("total", total);

        return R.ok(resultMap);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id){
        ootdImageService.removeById(id);
        return R.ok();
    }
}
