package com.javamall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.javamall.entity.*;
import com.javamall.service.IBigTypeService;
import com.javamall.service.ISmallTypeService;
import com.javamall.util.DateUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理端-商品大类Controller控制器
 */
@RestController
@RequestMapping("/admin/bigType")
public class AdminBigTypeController {

    @Autowired
    private IBigTypeService bigTypeService;

    @Autowired
    private ISmallTypeService smallTypeService;

    @Value("${bigTypeImagesFilePath}")
    private String bigTypeImagesFilePath;

    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<BigType> page = new Page<>(pageBean.getPageNum(), pageBean.getPageSize());
        Page<BigType> pageResult = bigTypeService.page(page, new QueryWrapper<BigType>().like("name", query));

        Map<String, Object> map = new HashMap<>();
        map.put("bigTypeList", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return R.ok(map);
    }

    /**
     * 查询所有商品大类
     */
    @RequestMapping("/listAll")
    public R listAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("bigTypeList", bigTypeService.list());
        return R.ok(map);
    }

    /**
     * 添加或者修改商品大类
     */
    @PostMapping("/save")
    public R save(@RequestBody BigType bigType) {
        if (bigType.getId() == null || bigType.getId() == -1) {
            bigTypeService.save(bigType);
        } else {
            bigTypeService.saveOrUpdate(bigType);
        }
        return R.ok();
    }

    /**
     * 删除商品大类
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id) {
        // 加个判断 大类下面如果有小类，返回报错提示
        if (smallTypeService.count(new QueryWrapper<SmallType>().eq("bigTypeId", id)) > 0) {
            return R.error(500, "大类下面有小类信息，不能删除");
        } else {
            bigTypeService.removeById(id);
            return R.ok();
        }
    }


    /**
     * 根据id查询
     */
    @GetMapping("/{id}")
    public R findById(@PathVariable(value = "id") Integer id) {
        System.out.println("id=" + id);
        BigType bigType = bigTypeService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("bigType", bigType);
        return R.ok(map);
    }

    /**
     * 上传商品大类图片
     */
    @RequestMapping("/uploadImage")
    public Map<String, Object> uploadImage(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            // 文件名拼接
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = DateUtil.getCurrentDateStr() + suffixName;

            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(bigTypeImagesFilePath + newFileName));

            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String, Object> dateMap = new HashMap<String, Object>();
            //返回给前端新的图片地址
            dateMap.put("title", newFileName);
            dateMap.put("src", "/image/bigType/" + newFileName);
            map.put("data", dateMap);
        }

        return map;
    }


}
