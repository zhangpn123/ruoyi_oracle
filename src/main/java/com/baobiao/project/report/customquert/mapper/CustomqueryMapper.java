package com.baobiao.project.report.customquert.mapper;

import java.util.List;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/12/29 13:55
 */
public interface CustomqueryMapper {
    /**
     * 定义分页查询
     * @param sql
     * @return
     */
    List selectList(String sql);
}
