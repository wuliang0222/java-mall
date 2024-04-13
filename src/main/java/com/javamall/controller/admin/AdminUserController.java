package com.javamall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.PageBean;
import com.javamall.entity.R;
import com.javamall.entity.WxUserInfo;
import com.javamall.service.IWxUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端-用户Controller控制器
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private IWxUserInfoService wxUserInfoService;

    /**
     * 根据条件分页查询用户信息
     *
     * @param pageBean
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean) {
        //查询条件
        String query = pageBean.getQuery().trim();
        Page<WxUserInfo> page = new Page<>(pageBean.getPageNum(), pageBean.getPageSize());
        Page<WxUserInfo> pageResult = wxUserInfoService.page(page, new QueryWrapper<WxUserInfo>().like("nickName", query));

        //查询结果
        Map<String, Object> map = new HashMap<>();
        map.put("userList", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return R.ok(map);
    }

}
