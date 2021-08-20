package com.baobiao.project.report.main.service;

import com.baobiao.project.report.dto.ReportCondition;
import com.baobiao.project.report.dto.ResponseMain;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2021/4/4 22:03
 */
public interface MainService {
    List<ResponseMain> selectData(List<String> fieldList, List<Map<String, Object>> replaceMap, ReportCondition reportCondition);

    List<Map<String, Object>> selectDataDetail(List<String> fieldList, List<Map<String, Object>> replaceMap, ReportCondition reportCondition);

}
