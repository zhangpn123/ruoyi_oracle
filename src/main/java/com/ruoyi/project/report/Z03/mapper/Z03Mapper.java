package com.ruoyi.project.report.Z03.mapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:38
 */
public interface Z03Mapper {

    LinkedList<Map<String, Object>> selBAccCode(Map paramsMap);

    List<Map<String, Object>> selectReport(Map paramsMap);

}
