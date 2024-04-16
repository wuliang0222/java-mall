package com.javamall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javamall.entity.OotdImage;

import java.util.List;
import java.util.Map;

/**
 * 订单主表Mapper接口
 */
public interface OotdImageMapper extends BaseMapper<OotdImage> {

    /**
     * 根据条件 分页查询订单
     *
     * @param map
     * @return
     */
    List<OotdImage> list(Map<String, Object> map);

    /**
     * 根据条件，查询订单总记录数
     *
     * @param map
     * @return
     */
    Long getTotal(Map<String, Object> map);

    OotdImage ootd(Map<String, Object> map);
}
