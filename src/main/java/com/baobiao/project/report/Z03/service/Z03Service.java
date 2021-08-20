package com.baobiao.project.report.Z03.service;

import com.baobiao.project.report.dto.ReportCondition;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:37
 */
public interface Z03Service {
    LinkedList<Map<String, Object>> selectRoleList(List<String> fieldList, List<Map<String,Object>> replaceMap, ReportCondition reportCondition);

}
