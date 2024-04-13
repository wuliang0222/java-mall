package com.javamall.util;

import com.javamall.entity.R;
import io.jsonwebtoken.Claims;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenUtil {

    public static R checkToken(String token) {
        Claims claims;
        //判断token是否为空
        if (StringUtil.isNotEmpty(token)) {
            //判断token是否失效
            claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            }
        } else {
            return R.error(500, "无权限访问！");
        }
        return R.ok(0, claims.getId());
    }
}
