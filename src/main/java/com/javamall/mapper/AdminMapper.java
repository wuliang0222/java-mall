package com.javamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javamall.entity.Admin;

/**
 * 管理员Mapper接口
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 修改
     */
    public Integer update(Admin admin);

}
