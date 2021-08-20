package com.baobiao.project.revice.analysis.service;

import com.baobiao.project.report.customquert.dto.CustomPageReq;
import com.baobiao.project.revice.dto.Report;

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
