package com.javamall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单主表
 */
@TableName("t_ootd_image")
@Data
public class OotdImage {

    private Integer id; // 编号

    private String orderNo; // 虚拟试衣号

    private String userId; // openId微信用户ID

    private String clothingImage; // 衣服图片

    private String bodyImage; // 身体图片

    private String ootdImage; // 虚拟试衣图片

    private Integer status=1; // 虚拟试衣状态 0 全部订单  1 未生成   2  已生成


}

