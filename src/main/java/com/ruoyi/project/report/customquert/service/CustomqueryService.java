package com.ruoyi.project.report.customquert.service;

import com.ruoyi.project.report.customquert.dto.CustomPageReq;

import java.util.List;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/12/29 13:53
 */
public interface CustomqueryService {
    /**
     * 自定义分页查询
     * @param customSql
     * @return
     */
    List selectList(String customSql);
}
