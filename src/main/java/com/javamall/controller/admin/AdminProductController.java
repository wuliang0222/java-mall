package com.javamall.controller.admin;


import com.javamall.entity.PageBean;
import com.javamall.entity.Product;
import com.javamall.entity.R;
import com.javamall.service.IProductService;
import com.javamall.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员-产品Controller控制器
 */
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private IProductService productService;

    @Value("${productImagesFilePath}")
    private String productImagesFilePath;

    @Value("${swiperImagesFilePath}")
    private String swiperImagesFilePath;

    /**
     * 根据条件分页查询
     *
     * @param pageBean
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", pageBean.getQuery().trim());
        map.put("start", (pageBean.getPageNum() - 1) * pageBean.getPageSize());
        map.put("pageSize", pageBean.getPageSize());

        // 条件查询
        List<Product> list = productService.list(map);
        Long total = productService.getTotal(map);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("productList", list);
        resultMap.put("total", total);
        return R.ok(resultMap);
    }

    /**
     * 更新热门状态
     *
     * @param id
     * @param hot
     * @return
     */
    @GetMapping("/updateHot/{id}/state/{hot}")
    public R updateHot(@PathVariable(value = "id") Integer id, @PathVariable(value = "hot") boolean hot) {
        Product product = productService.getById(id);
        product.setHot(hot);
        if (hot) {
            product.setHotDateTime(new Date());
        } else {
            product.setHotDateTime(null);
        }
        productService.saveOrUpdate(product);
        return R.ok();
    }

    /**
     * 更新首页轮播图状态
     *
     * @param id
     * @param swiper
     * @return
     */
    @GetMapping("/updateSwiper/{id}/state/{swiper}")
    public R updateSwiper(@PathVariable(value = "id") Integer id, @PathVariable(value = "swiper") boolean swiper) {
        Product product = productService.getById(id);
        product.setSwiper(swiper);
        productService.saveOrUpdate(product);
        return R.ok();
    }

    /**
     * 上传商品图片
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadImage")
    public Map<String, Object> uploadImage(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "product" + DateUtil.getCurrentDateStr() + suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(productImagesFilePath + newFileName));

            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("title", newFileName);
            map2.put("src", "/image/product/" + newFileName);
            map.put("data", map2);
        }
        return map;
    }

    /**
     * 修改商品图片
     *
     * @param product
     * @return
     */
    @PostMapping("/saveImage")
    public R saveImage(@RequestBody Product product) {
        Product p = productService.getById(product.getId());
        p.setProPic(product.getProPic());
        productService.saveOrUpdate(p);
        return R.ok();
    }

    /**
     * 上传首页轮播图
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadSwiperImage")
    public Map<String, Object> uploadSwiperImage(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "swiper" + DateUtil.getCurrentDateStr() + suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(swiperImagesFilePath + newFileName));

            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("title", newFileName);
            //虚拟请求路径
            map2.put("src", "/image/swiper/" + newFileName);
            map.put("data", map2);
        }
        return map;
    }

    /**
     * 更新首页轮播图
     *
     * @param product
     * @return
     */
    @PostMapping("/saveSwiper")
    public R saveSwiper(@RequestBody Product product) {
        Product p = productService.getById(product.getId());
        p.setSwiperPic(product.getSwiperPic());
        p.setSwiperSort(product.getSwiperSort());
        productService.saveOrUpdate(p);
        return R.ok();
    }

    /**
     * 添加、编辑商品信息
     *
     * @param product
     * @return
     */
    @PostMapping("/save")
    public R save(@RequestBody Product product) {
        if (product.getId() == null || product.getId() == -1) { // 添加
            productService.add(product);
        } else { // 编辑
            productService.update(product);
        }
        return R.ok();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id) {
        productService.removeById(id);
        return R.ok();
    }

}
