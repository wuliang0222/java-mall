package com.javamall.entity;

import lombok.Data;

/**
 * 分页Model类
 */
@Data
public class PageBean {

    private int pageNum; // 当前页
    private int pageSize; // 每页大小
    private int start;  // 起始页
    private String query; // 查询参数

    public PageBean() {
    }

    public PageBean(int pageNum, int pageSize, String query) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.query = query;
    }

    public PageBean(int pageNum, int pageSize) {
        super();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
