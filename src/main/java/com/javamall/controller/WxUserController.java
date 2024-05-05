package com.javamall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.javamall.constant.SystemConstant;
import com.javamall.entity.R;
import com.javamall.entity.WxUserInfo;
import com.javamall.properties.WeixinProperties;
import com.javamall.service.IWxUserInfoService;
import com.javamall.util.HttpClientUtil;
import com.javamall.util.JwtUtils;
import com.javamall.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信用户Controller
 */
@RestController
@RequestMapping("/user")
public class WxUserController {

    @Autowired
    private WeixinProperties weixinProperties;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private IWxUserInfoService wxUserInfoService;

    /**
     * 验证token超时
     * @param token 之前生成的token
     */
    @RequestMapping("/validate")
    public R validate(@RequestHeader(value = "token") String token) {
        return TokenUtil.checkToken(token);
    }


    /**
     * 微信用户登录
     * @param wxUserInfo [code,nickName,avatarUrl]
     * @return token
     */
    @RequestMapping("/wxlogin")
    public R wxLogin(@RequestBody WxUserInfo wxUserInfo) {
        // 用code拼接微信请求api，获取openid
        String jscode2sessionUrl = weixinProperties.getJscode2sessionUrl() + "?appid=" + weixinProperties.getAppid() + "&secret=" + weixinProperties.getSecret() + "&js_code=" + wxUserInfo.getCode() + "&grant_type=authorization_code";
        String result = httpClientUtil.sendHttpGet(jscode2sessionUrl);
        JSONObject jsonObject = JSON.parseObject(result);
        String openid = jsonObject.get("openid").toString();

        // 插入用户到数据库 判断用户是否存在 不存在的话 插入 存在的话 更新
        WxUserInfo resultWxUserInfo = wxUserInfoService.getOne(new QueryWrapper<WxUserInfo>().eq("openid", openid));
        if (resultWxUserInfo == null) { // 不存在 插入用户
            wxUserInfo.setOpenid(openid);
            wxUserInfo.setRegisterDate(new Date());
            wxUserInfo.setLastLoginDate(new Date());
            wxUserInfoService.save(wxUserInfo);
        } else {  // 存在 更新用户信息
            resultWxUserInfo.setNickName(wxUserInfo.getNickName());
            resultWxUserInfo.setAvatarUrl(wxUserInfo.getAvatarUrl());
            resultWxUserInfo.setLastLoginDate(new Date());
            wxUserInfoService.updateById(resultWxUserInfo);
        }

        // 利用jwt生成token返回到前端
        String token = JwtUtils.createJWT(openid, wxUserInfo.getNickName(), SystemConstant.JWT_TTL);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        return R.ok(resultMap);
    }

}
