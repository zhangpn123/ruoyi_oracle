package com.ruoyi.project.report.report.mapper;

import com.ruoyi.project.report.report.domain.ReportRsp;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:38
 */
public interface ReportMapper {

    List<ReportRsp> getData(Map paramsMap);
}