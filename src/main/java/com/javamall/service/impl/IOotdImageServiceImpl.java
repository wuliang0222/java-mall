package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.OotdImage;
import com.javamall.mapper.OotdImageMapper;
import com.javamall.service.IOotdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    public String ootd(String model, String garment, int category) throws Exception {
        String pythonScriptPath = "F:\\300-Program\\310-Code\\ootd\\ootd_dc.py";
//        String model = "F:/300-Program/310-Code/ootd/img/model_half.png";
//        String garment = "F:/300-Program/310-Code/ootd/img/up.jpg";
        String[] categorys = {"Upper-body", "Lower-body", "Dress"};
//        String category = "Upper-body";

        // 创建参数列表
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(pythonScriptPath);
        command.add(model);
        command.add(garment);
        command.add(categorys[category]);

        System.out.println("java-ootd启动");

        // 启动进程

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                System.err.println("Python脚本执行超时");
                process.destroy();
                throw new Exception("生成失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结束");
        return "2";
    }
}
