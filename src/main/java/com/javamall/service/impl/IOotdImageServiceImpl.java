package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.OotdImage;
import com.javamall.entity.Product;
import com.javamall.mapper.OotdImageMapper;
import com.javamall.service.IOotdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

    @Autowired
    private IOotdImageService ootdImageService;

    @Override
    public List<OotdImage> list(Map<String, Object> map) {
        return ootdImageMapper.list(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return ootdImageMapper.getTotal(map);
    }

    @Override
    @Async
    public void ootd(String model, String garment, Integer category, OotdImage ootdImage) {
        String pythonScriptPath = "F:\\300-Program\\310-Code\\ootd\\ootd_dc.py";
        String URL = "F:/300-Program/310-Code/images/";
        String[] categories = {"Upper-body", "Lower-body", "Dress"};

        // 创建参数列表
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(pythonScriptPath);
        command.add(URL + "bodyImgs/" + model);
        command.add(URL + "clothingImgs/" + garment);
        command.add(categories[category]);
        System.out.println("java-ootd启动");
        System.out.println(URL + "bodyImgs/" + model);
        System.out.println(URL + "clothingImgs/" + garment);
        System.out.println(categories[category]);
        String ootdImageUrl = null;
        // 启动进程
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                ootdImageUrl = line;
                System.out.println("Python 脚本输出：" + ootdImageUrl);
            }

            // 超时处理
            if (!process.waitFor(5, TimeUnit.MINUTES)) { // 超时
                System.err.println("Python脚本执行超时");
                process.destroy();
                throw new Exception("生成失败");
            } else { //正常执行
                OotdImage p = ootdImageService.getById(ootdImage.getId());
                p.setOotdImage(ootdImageUrl);
                p.setStatus(2);
                ootdImageService.saveOrUpdate(p);
                System.out.println("python结束");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
