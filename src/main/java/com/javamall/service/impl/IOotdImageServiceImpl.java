package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.OotdImage;
import com.javamall.mapper.OotdImageMapper;
import com.javamall.service.IOotdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 虚拟试衣Service实现类
 */
@Service("ootdImageService")
public class IOotdImageServiceImpl extends ServiceImpl<OotdImageMapper, OotdImage> implements IOotdImageService {

    @Autowired
    private OotdImageMapper ootdImageMapper;

    @Override
    public List<OotdImage> list(Map<String, Object> map) {
        return ootdImageMapper.list(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return ootdImageMapper.getTotal(map);
    }

    @Override
    public OotdImage ootd(Map<String, Object> map) {
        return ootdImageMapper.ootd(map);
    }
}
