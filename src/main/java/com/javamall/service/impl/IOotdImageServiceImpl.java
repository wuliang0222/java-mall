package com.javamall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javamall.entity.OotdImage;
import com.javamall.entity.Product;
import com.javamall.mapper.OotdImageMapper;
import com.javamall.service.IOotdImageService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String ootd(String model, String garment, Integer category, OotdImage ootdImage) {
        String pythonScriptPath = "F:\\300-Program\\310-Code\\ootd\\ootd_dc.py";
        String URL = "file:F:/300-Program/310-Code/images/";
        String[] categorys = {"Upper-body", "Lower-body", "Dress"};
        System.out.println("model" + model);
        System.out.println("garment" + garment);
        // 创建参数列表
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(pythonScriptPath);
        command.add(URL + "bodyImgs/" + model);
        command.add(URL + "clothingImgs/" + garment);
        command.add(categorys[category]);
        System.out.println("java-ootd启动");
        String ootdImageUrl = null;
        // 启动进程
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                String line = reader.readLine(); // 读取一行内容
                if (line == null) {
                    break; // 如果读到了文件结尾，退出循环
                }
                ootdImageUrl = line; // 保存读取到的内容到变量中
                System.out.println("Python 脚本输出：" + ootdImageUrl);
            }
            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                System.err.println("Python脚本执行超时");
                process.destroy();
                throw new Exception("生成失败");
            } else { //正常执行
                System.out.println("ootdImageUrl" + ootdImageUrl);
                System.out.println("ootdImage.getId()" + ootdImage.getId());
                OotdImage p = ootdImageService.getById(ootdImage.getId());
                p.setOotdImage(ootdImageUrl);
                p.setStatus(2);
                ootdImageService.saveOrUpdate(p);
                System.out.println("python结束");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "2";
    }
}
