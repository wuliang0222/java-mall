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

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("bigTypeList", pageResult.getRecords());
        resultMap.put("total", pageResult.getTotal());
        return R.ok(resultMap);
    }

    /**
     * 查询所有商品大类
     */
    @RequestMapping("/listAll")
    public R listAll() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("bigTypeList", bigTypeService.list());
        return R.ok(resultMap);
    }

    /**
     * 添加或者修改商品大类
     */
    @PostMapping("/save")
    public R save(@RequestBody BigType bigType) {
        bigTypeService.saveOrUpdate(bigType);
        return R.ok();
    }

    /**
     * 删除商品大类
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id) {
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
        BigType bigType = bigTypeService.getById(id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("bigType", bigType);
        return R.ok(resultMap);
    }

    /**
     * 上传商品大类图片
     */
    @RequestMapping("/uploadImage")
    public Map<String, Object> uploadImage(MultipartFile file) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String suffixName = null;
            if (fileName != null) {
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            }
            String newFileName = "bigType" + DateUtil.getCurrentDateStr() + suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(bigTypeImagesFilePath + newFileName));

            resultMap.put("code", 0);
            resultMap.put("msg", "上传成功");
            Map<String, Object> dateMap = new HashMap<>();
            dateMap.put("title", newFileName);
            dateMap.put("src", "/image/bigType/" + newFileName);
            resultMap.put("data", dateMap);
        }
        return resultMap;
    }


}
