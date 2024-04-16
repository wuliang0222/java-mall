package com.javamall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.*;
import com.javamall.service.IOotdImageService;
import com.javamall.util.DateUtil;
import com.javamall.util.TokenUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * 虚拟试衣Controller控制器
 */
@RestController
@RequestMapping("/my/ootd")
public class OotdImageController {

    @Autowired
    private IOotdImageService ootdImageService;

    @Value("${bodyImagesFilePath}")
    private String bodyImagesFilePath;

    @Value("${clothingImagesFilePath}")
    private String clothingImagesFilePath;

    /**
     * 上传换装前图片
     */
    @RequestMapping("/uploadImage")
    public Map<String, Object> uploadOotdImageUpper(@RequestParam("file") MultipartFile file, @RequestParam Map<String, String> formData) throws Exception {
        String prefixName = formData.get("type");
        Map<String, Object> resultMap = new HashMap<>();
        if (!file.isEmpty()) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = null;
            if (fileName != null) {
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            }
            // 用前缀+时间戳作为文件名
            String newFileName = prefixName + DateUtil.getCurrentDateStr() + suffixName;
            resultMap.put("code", 0);
            resultMap.put("msg", "上传成功");

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("title", newFileName);
            // 根据prefixName判断图片种类 保存到对应磁盘文件
            if (prefixName.equals("model")) { // 身体
                FileUtils.copyInputStreamToFile(file.getInputStream(), new File(bodyImagesFilePath + newFileName));
                dataMap.put("src", newFileName);
            } else if (prefixName.equals("garment")) { //衣服
                FileUtils.copyInputStreamToFile(file.getInputStream(), new File(clothingImagesFilePath + newFileName));
                dataMap.put("src", newFileName);
            }
            resultMap.put("data", dataMap);
        }
        return resultMap;
    }

    /**
     * 创建虚拟试衣，返回虚拟试衣号
     */
    @RequestMapping("/create")
    public R create(@RequestBody OotdImage ootdImage, @RequestHeader(value = "token") String token) {
        // 判断token
        R r = TokenUtil.checkToken(token);
        if (r.get("code").equals(500)) return r;
        String openId = r.get("msg").toString();

        ootdImage.setUserId(openId);
        ootdImage.setOotdNo("ootd" + DateUtil.getCurrentDateStr());
        ootdImage.setCreateDate(new Date());
        ootdImageService.save(ootdImage);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("ootdNo", ootdImage.getOotdNo());

        //ootd时间比较久 单独创建一个线程
        new Thread(() -> {
            String bodyImage = ootdImage.getBodyImage();
            String clothingImage = ootdImage.getClothingImage();
            Integer category = ootdImage.getCategory();
            ootdImageService.ootd(bodyImage, clothingImage, category, ootdImage);
        }).start();

        return R.ok(resultMap);
    }


    /**
     * 虚拟试衣查询  0全部虚拟试衣  1未生成  2已生成
     */
    @RequestMapping("/list")
    public R list(Integer type, Integer page, Integer pageSize, @RequestHeader(value = "token") String token) {
        // 判断token
        R r = TokenUtil.checkToken(token);
        if (r.get("code").equals(500)) return r;
        String openId = r.get("msg").toString();

        Page<OotdImage> pageOotdImage = new Page<>(page, pageSize);
        Page<OotdImage> ootdImageResult;
        if (type == 0) {  // 查询全部
            ootdImageResult = ootdImageService.page(pageOotdImage, new QueryWrapper<OotdImage>().eq("userId", openId).orderByDesc("ootdNo"));
        } else {  // 根据状态查询
            ootdImageResult = ootdImageService.page(pageOotdImage, new QueryWrapper<OotdImage>().eq("userId", openId).eq("status", type).orderByDesc("ootdNo"));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", ootdImageResult.getTotal());
        resultMap.put("totalPage", +ootdImageResult.getPages());
        resultMap.put("page", page);
        List<OotdImage> ootdImageList = ootdImageResult.getRecords();
        resultMap.put("ootdImageList", ootdImageList);
        return R.ok(resultMap);
    }


    /**
     * 显示所有生成的虚拟试衣
     */
    @RequestMapping("/listAll")
    public R listAll(Integer page, Integer pageSize) {
        Page<OotdImage> pageOotdImage = new Page<>(page, pageSize);
        Page<OotdImage> ootdImageResult = ootdImageService.page(pageOotdImage, new QueryWrapper<OotdImage>().eq("status", 2).orderByDesc("ootdNo"));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", ootdImageResult.getTotal());
        resultMap.put("totalPage", +ootdImageResult.getPages());
        resultMap.put("page", page);
        List<OotdImage> ootdImageList = ootdImageResult.getRecords();
        resultMap.put("ootdImageList", ootdImageList);
        return R.ok(resultMap);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(Integer id) {
        ootdImageService.removeById(id);
        return R.ok();
    }
}
