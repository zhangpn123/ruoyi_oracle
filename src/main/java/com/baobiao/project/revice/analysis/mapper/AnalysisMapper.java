package com.baobiao.project.revice.analysis.mapper;

import com.baobiao.project.report.customquert.dto.CustomPageReq;
import com.baobiao.project.revice.dto.Report;
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

    /**
     * 批量插入所有数据
     * @param params
     * @return
     */
    int batchInsert(@Param("list") List<Report> params);

    /**
     * 清除旧数据
     * @return
     */
    int deleteAll();
}
