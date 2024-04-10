package com.javamall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.javamall.entity.OotdImage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 虚拟试衣Service接口
 */
public interface IOotdImageService extends IService<OotdImage> {
    /**
     * 根据条件 分页查询订虚拟试衣
     * @param map
     * @return
     */
    public List<OotdImage> list(Map<String,Object> map);

    /**
     * 根据条件，查询虚拟试衣总记录数
     * @param map
     * @return
     */
    public Long getTotal(Map<String,Object> map);

    String ootd(String model, String garment, int category) throws Exception;
}
