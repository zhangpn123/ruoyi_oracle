package com.ruoyi.project.report.Z05.service;

import com.ruoyi.project.report.Z03.domain.Z03Report;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/12/7 9:08
 */
public interface Z05Service {
    /**
     * 查询数据
     * @param fieldList
     * @param replaceMap
     * @param z03Report
     * @return
     */
    List<Map<String, Object>> selectRoleList(List<String> fieldList, List<Map<String, Object>> replaceMap, Z03Report z03Report);
}
