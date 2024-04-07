package com.javamall.entity;

import io.jsonwebtoken.Claims;
import lombok.Data;

/**
 * jwt验证信息
 */
@Data
public class CheckResult {

    private int errCode;

    private boolean success;

    private Claims claims;

}
