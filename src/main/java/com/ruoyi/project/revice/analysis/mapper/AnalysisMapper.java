package com.ruoyi.project.revice.analysis.mapper;

import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import com.ruoyi.project.revice.dto.Report;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-03-11 13:46
 **/
public interface AnalysisMapper {

    List<Report> selectList(CustomPageReq customPageReq);

    List<Report> selectDetailedList(Report report);

    Report selectReport(@Param("coCode") String coCode);

    List<Report> selectQueTypeByCode(@Param("coCode") String coCode);

    int batchInsert(@Param("list") List<Report> params);
}
