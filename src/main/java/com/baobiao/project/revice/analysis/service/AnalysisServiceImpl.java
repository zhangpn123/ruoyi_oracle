package com.baobiao.project.revice.analysis.service;

import com.baobiao.project.report.customquert.dto.CustomPageReq;
import com.baobiao.project.revice.analysis.mapper.AnalysisMapper;
import com.baobiao.project.revice.dto.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-03-11 10:46
 **/
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private AnalysisMapper analysisMapper;
    @Override
    public List<Report> selectList(CustomPageReq customPageReq) {
        return analysisMapper.selectList(customPageReq);
    }

    @Override
    public Report selectReport(String coCode) {
        return analysisMapper.selectReport(coCode);
    }

    @Override
    public List<Report> selectDetailedList(Report report) {
        return analysisMapper.selectDetailedList(report);
    }

    @Override
    public List<Report> selectQueTypeByCode(String coCode) {
        return analysisMapper.selectQueTypeByCode(coCode);
    }

    @Override
    public int batchInsert(List<Report> params) {
        /*将旧数据删除*/
        analysisMapper.deleteAll();
        return analysisMapper.batchInsert(params);
    }
}
