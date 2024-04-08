package com.javamall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.BigType;
import com.javamall.entity.PageBean;
import com.javamall.entity.R;
import com.javamall.entity.SmallType;
import com.javamall.service.IBigTypeService;
import com.javamall.service.ISmallTypeService;
import com.javamall.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/my/ootd")
public class OotdController {

    @Value("${ootdImagesFilePath}")
    private String ootdImagesFilePath;

    /**
     * 上传换装前图片
     */
    @RequestMapping("/uploadImage/upper")
    public Map<String, Object> uploadOotdImageUpper(MultipartFile file) throws Exception {
        System.out.println(123);
        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            System.out.println("fileName" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 用时间戳作为文件名
            String newFileName = "upper" + DateUtil.getCurrentDateStr() + suffixName;

            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(ootdImagesFilePath + newFileName));
            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("title", newFileName);
            map2.put("src", "/image/ootdImgs/" + newFileName);
            map.put("data", map2);
        }
        System.out.println("map:" + map);
        System.out.println("file2");
        return map;
    }


}
