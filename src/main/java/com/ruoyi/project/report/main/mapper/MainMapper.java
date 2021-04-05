package com.ruoyi.project.report.main.mapper;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2021/4/5 1:20
 */
public interface MainMapper {

    List<Map<String, Object>> selectReport(Map<String, Object> beanMap);

    List<Map<String, Object>> selectDataDetail(Map<String, Object> beanMap);
}
