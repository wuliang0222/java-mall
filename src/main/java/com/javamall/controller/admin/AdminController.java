package com.javamall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.javamall.constant.SystemConstant;
import com.javamall.entity.Admin;
import com.javamall.entity.R;
import com.javamall.service.IAdminService;
import com.javamall.util.JwtUtils;
import com.javamall.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员Controller
 */
@RestController
public class AdminController {

    @Autowired
    private IAdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/adminLogin")
    public R adminLogin(@RequestBody Admin admin){
        if(admin==null){
            return R.error();
        }
        if(StringUtil.isEmpty(admin.getUserName())){
            return R.error("用户名不能为空！");
        }
        if(StringUtil.isEmpty(admin.getPassword())){
            return R.error("密码不能为空！");
        }

        Admin resultAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("userName", admin.getUserName()));
        if(resultAdmin==null){
            return R.error("用户名不存在！");
        }
        if(!resultAdmin.getPassword().trim().equals(admin.getPassword())){
            return R.error("用户名或者密码错误！");
        }

        //用户名密码正确 生成token
        String token = JwtUtils.createJWT("-1", "admin", SystemConstant.JWT_TTL);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("token",token);
        return R.ok(resultMap);
    }

    /**
     * 修改密码
     */
    @PostMapping("/admin/modifyPassword")
    public R modifyPassword(@RequestBody Admin admin){
        if(StringUtil.isEmpty(admin.getUserName())){
            return R.error("用户名不能为空！");
        }
        if(StringUtil.isEmpty(admin.getNewPassword())){
            return R.error("新密码不能为空！");
        }
        adminService.update(admin);
        return R.ok();
    }

}
