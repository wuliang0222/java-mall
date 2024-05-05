package com.javamall.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回值实体类封装
 */
public class R extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
    }

    /**
     * 默认失败
     */
    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    /**
     * 自定义失败信息
     */
    public static R error(String msg) {
        return error(500, msg);
    }

    /**
     * 自定义失败码、失败信息
     */
    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    /**
     * 默认成功
     */
    public static R ok() {
        return new R();
    }

    /**
     * 自定义成功信息
     */
    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    /**
     * 自定义成功码、成功信息
     */
    public static R ok(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    /**
     * 设置多个返回信息的键值对
     */
    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    /**
     * 设置返回信息的键值对
     */
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
