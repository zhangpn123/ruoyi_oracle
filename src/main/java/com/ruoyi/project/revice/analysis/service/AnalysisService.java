package com.ruoyi.project.revice.analysis.service;

import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import com.ruoyi.project.revice.dto.Report;
import com.ruoyi.project.system.dict.domain.DictData;

import java.util.List;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-03-11 10:45
 **/
public interface AnalysisService {

    List<Report> selectList(CustomPageReq customPageReq);

    Report selectReport(String coCode);

    List<Report> selectDetailedList(Report report);

    List<Report> selectQueTypeByCode(String coCode);

    int batchInsert(List<Report> params);
}
