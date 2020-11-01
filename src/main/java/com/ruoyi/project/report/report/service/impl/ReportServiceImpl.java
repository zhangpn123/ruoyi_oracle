package com.ruoyi.project.report.report.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.report.domain.ReportRsp;
import com.ruoyi.project.report.report.mapper.ReportMapper;
import com.ruoyi.project.report.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2020-09-18 10:37
 **/
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public List<ReportRsp> getData(Map paramsMap) {
        return reportMapper.getData(paramsMap);
    }


}
