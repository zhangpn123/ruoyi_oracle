package com.ruoyi.project.report.Z03.mapper;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:38
 */
public interface Z03Mapper {

    List<Map<String, Object>> selBAccCode();

    List<Map<String, Object>> selectReport(Map paramsMap);

}
