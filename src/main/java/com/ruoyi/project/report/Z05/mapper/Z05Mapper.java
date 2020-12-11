package com.ruoyi.project.report.Z05.mapper;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/12/7 9:29
 */
public interface Z05Mapper {
    /**
     * 根据条件查询结果
     * @param paramsMap
     * @return
     */
    Map<String, Object> selectByCondition(Map paramsMap);
}
