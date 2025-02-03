package com.example.sports.vo;

import lombok.Data;
import java.util.List;

@Data
public class PageVO<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public PageVO(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }
} 