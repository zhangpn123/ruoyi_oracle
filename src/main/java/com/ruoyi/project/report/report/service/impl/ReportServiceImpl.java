package com.ruoyi.project.report.report.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.report.Z03.mapper.Z03Mapper;
import com.ruoyi.project.report.report.domain.ReportRsp;
import com.ruoyi.project.report.report.mapper.ReportMapper;
import com.ruoyi.project.report.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private Z03Mapper z03Mapper;

    @Override
    public List<ReportRsp> getData(Map paramsMap) {
        return reportMapper.getData(paramsMap);
    }

    @Override
    public List<Map<String, Object>> getBAccCode() {
        return z03Mapper.selBAccCode();
    }

    @Override
    public Map<String, Object> getDateByCondition(Map<String, Object> paramMap) {
        List<Map<String, Object>> dateByCondition = reportMapper.getDateByCondition(paramMap);
        Map  resMap = new HashMap<>();
        /*封装数据*/
        if(dateByCondition != null && dateByCondition.size() > 0){
            for (Map<String, Object> map : dateByCondition) {
                String bAccCode = StringUtils.getObjStr(map.get("bAccCode"));
                BigDecimal caAmt = new BigDecimal(StringUtils.getObjStr(map.get("caAmt")));
                    if (resMap.containsKey(bAccCode)){
                        resMap.put(bAccCode, new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode))).add(caAmt).setScale(2, BigDecimal.ROUND_HALF_UP).toString() );
                    }else{
                        resMap.put(bAccCode,caAmt);
                    }
            }
        }
        return resMap;
    }


}
