package com.ruoyi.project.report.report.service;

import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.report.domain.ReportRsp;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:37
 */
public interface ReportService {

    /**
     *
     * @param paramsMap
     * @return
     */
    List<ReportRsp> getData(Map paramsMap);
}