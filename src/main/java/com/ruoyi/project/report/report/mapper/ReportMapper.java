package com.ruoyi.project.report.report.mapper;

import com.ruoyi.project.report.report.domain.ReportRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:38
 */
public interface ReportMapper {

    List<ReportRsp> getData(Map paramsMap);


    List<Map<String, Object>> getDateByCondition(Map<String, Object> paramMap);

    Map<String, Object> getDataByAccCode(Map paramsMap);

    Map<String, Object> getCRAmt(Map<String, Object> paramMap);
}
