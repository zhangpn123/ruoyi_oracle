package com.ruoyi.project.report.customquert.mapper;

import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import org.apache.ibatis.annotations.Param;

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
