package com.javamall.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("t_show_image")
@Data
public class ShowImage {

    private Integer id; // 编号

    private String openid; // openId微信用户ID

    private String image = "default.jpg"; // 商品图片
}
