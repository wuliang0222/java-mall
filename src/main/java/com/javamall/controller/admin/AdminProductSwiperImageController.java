package com.javamall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.javamall.entity.ProductSwiperImage;
import com.javamall.entity.R;
import com.javamall.service.IProductSwiperImageService;
import com.javamall.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员-产品轮播图Controller控制器
 */
@RestController
@RequestMapping("/admin/productSwiperImage")
public class AdminProductSwiperImageController {

    @Autowired
    private IProductSwiperImageService productSwiperImageService;

    @Value("${productSwiperImagesFilePath}")
    private String productSwiperImagesFilePath;

    /**
     * 根据条件查询
     */
    @GetMapping("/list/{id}")
    public R list(@PathVariable(value = "id") Integer id) {
        List<ProductSwiperImage> list = productSwiperImageService.list(new QueryWrapper<ProductSwiperImage>().eq("productId", id));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("productSwiperImageList", list);
        return R.ok(resultMap);
    }

    /**
     * 上传详情轮播图
     */
    @RequestMapping("/uploadImage")
    public Map<String, Object> uploadImage(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String suffixName = null;
            if (fileName != null) {
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            }
            String newFileName = "productSwiper" + DateUtil.getCurrentDateStr() + suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(productSwiperImagesFilePath + newFileName));

            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String, Object> dateMap = new HashMap<>();
            dateMap.put("title", newFileName);
            dateMap.put("src", "/image/productSwiperImgs/" + newFileName);
            map.put("data", dateMap);
        }
        return map;
    }

    /**
     * 添加详情轮播图
     */
    @PostMapping("/add")
    public R add(@RequestBody ProductSwiperImage productSwiperImage) {
        productSwiperImageService.saveOrUpdate(productSwiperImage);
        return R.ok();
    }

    /**
     * 删除
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id) {
        productSwiperImageService.removeById(id);
        return R.ok();
    }


}
