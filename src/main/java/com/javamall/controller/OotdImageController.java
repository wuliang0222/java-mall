package com.javamall.controller;


import com.javamall.entity.*;
import com.javamall.service.IOotdImageService;
import com.javamall.util.DateUtil;
import com.javamall.util.JwtUtils;
import com.javamall.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟试衣Controller控制器
 */
@RestController
@RequestMapping("/my/ootd")
public class OotdImageController {

    @Autowired
    private IOotdImageService ootdImageService;

    @Value("${ootdImagesFilePath}")
    private String ootdImagesFilePath;


    /**
     * 创建订单，返回订单号
     * @return
     */
    @RequestMapping("/create")
    public R create(@RequestBody OotdImage ootdImage, @RequestHeader(value = "token") String token) {
        //判断token是否为空
        if (StringUtil.isNotEmpty(token)) {
            //判断token是否失效
            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims != null) {
                String openId = claims.getId();
                ootdImage.setUserId(openId);
                ootdImage.setOotdNo("ootd" + DateUtil.getCurrentDateStr());
            } else {
                return R.error(500, "鉴权失败！");
            }
        } else {
            return R.error(500, "无权限访问！");
        }

        ootdImageService.save(ootdImage);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String ootdNo = ootdImage.getOotdNo();
        resultMap.put("ootdNo", ootdNo);
        return R.ok(resultMap);
    }

    /**
     * 上传换装前图片
     */
    @RequestMapping("/uploadImage/upper")
    public Map<String, Object> uploadOotdImageUpper(@RequestParam("file")MultipartFile file,@RequestParam  Map<String, String> formData) throws Exception {
        System.out.println("file:"+file);
        System.out.println("testImage:"+formData.get("testImage"));
        System.out.println("testImage2:"+ formData.get("testImage2"));

        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            System.out.println("fileName:" + fileName);
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
        return map;
    }


}
