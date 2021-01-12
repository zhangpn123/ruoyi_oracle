package com.ruoyi.project.report.report.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.report.Z03.mapper.Z03Mapper;
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
        Map resMap = new HashMap<>();
        if (StringUtils.getobjBoolean(paramMap.get("isItemCode"))){
            List<Map<String, Object>> dateByCondition = reportMapper.getItemCodeDateByCondition(paramMap);
            /*封装数据*/
            if (dateByCondition != null && dateByCondition.size() > 0) {
                for (Map<String, Object> map : dateByCondition) {
                    String bAccCode = StringUtils.getObjStr(map.get("bAccCode"));
                    String itemCode = StringUtils.getObjStr(map.get("itemCode"));
                    BigDecimal caAmt = new BigDecimal(StringUtils.getObjStrBigDeci(map.get("caAmt")));
                    if (resMap.containsKey(bAccCode+"/"+itemCode)) {
                        resMap.put(bAccCode+"/"+itemCode, new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode+"/"+itemCode))).add(caAmt).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        resMap.put(bAccCode+"/"+itemCode, caAmt);
                    }
                }
            }
        }else {
            List<Map<String, Object>> dateByCondition = reportMapper.getDateByCondition(paramMap);
            /*封装数据*/
            if (dateByCondition != null && dateByCondition.size() > 0) {
                for (Map<String, Object> map : dateByCondition) {
                    String bAccCode = StringUtils.getObjStr(map.get("bAccCode"));
                    BigDecimal caAmt = new BigDecimal(StringUtils.getObjStrBigDeci(map.get("caAmt")));
                    if (resMap.containsKey(bAccCode)) {
                        resMap.put(bAccCode, new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode))).add(caAmt).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        resMap.put(bAccCode, caAmt);
                    }
                }
            }
        }

        return resMap;
    }

    @Override
    public BigDecimal getDataByAccCode(Map paramsMap) {
        String accCode = StringUtils.getObjStr(paramsMap.get("acc_code"));
        BigDecimal sum = new BigDecimal("0.00");
        /*判断是否有多个数据拼接*/
        if (accCode.contains("+")) {
            String[] split = accCode.split("\\+");
            for (int i = 0; i < split.length; i++) {
                paramsMap.put("acc_code", split[i]);
                Map<String, Object> resMap = reportMapper.getDataByAccCode(paramsMap);
                if (null != resMap && 0 < resMap.size()) {
                     String crAmt = StringUtils.getObjStrBigDeci(resMap.get("crAmt"));
                     sum = sum.add(new BigDecimal(crAmt));
                    }
            }
        } else if (accCode.contains("-")) {
            String[] split = accCode.split("-");
            for (int i = 0; i < split.length; i++) {
                paramsMap.put("acc_code", split[i]);
                Map<String, Object> resMap = reportMapper.getDataByAccCode(paramsMap);
                if (null != resMap && 0 < resMap.size()) {
                        String crAmt = StringUtils.getObjStrBigDeci(resMap.get("crAmt"));
                        if (i == 0) {
                            sum = sum.add(new BigDecimal(crAmt));
                        }
                        sum = sum.subtract(new BigDecimal(crAmt));
                }
            }
        } else {
           Map<String, Object> resMap = reportMapper.getDataByAccCode(paramsMap);
            if (null != resMap && 0 < resMap.size()) {
                    String crAmt = StringUtils.getObjStrBigDeci(resMap.get("crAmt"));
                    sum = sum.add(new BigDecimal(crAmt));
            }
        }
        return sum;
    }

    @Override
    public BigDecimal getCRAmt(Map<String, Object> paramMap) {
        BigDecimal result = new BigDecimal("0.00");
        String acc_code = StringUtils.getObjStr(paramMap.get("acc_code"));
        if (acc_code.contains("+")) {
            String[] split = acc_code.split("\\+");
            for (int i = 0; i < split.length; i++) {
                paramMap.put("acc_code", split[i]);
                Map<String, Object> crAmtMap = reportMapper.getCRAmt(paramMap);
                if (null != crAmtMap && 0 < crAmtMap.size()) {
                        String crAmt = StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt"));
                        result = result.add(new BigDecimal(crAmt));
                }
            }
        } else if (acc_code.contains("-")) {
            String[] split = acc_code.split("-");
            for (int i = 0; i < split.length; i++) {
                paramMap.put("acc_code", split[i]);
                Map<String, Object> crAmtMap = reportMapper.getCRAmt(paramMap);
                if (null != crAmtMap && 0 < crAmtMap.size()) {
                        String crAmt = StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt"));
                        if (i == 0) {
                            result = result.add(new BigDecimal(crAmt));
                        }
                        result = result.subtract(new BigDecimal(crAmt));
                }
            }
        } else {
            Map<String, Object> crAmtMap = reportMapper.getCRAmt(paramMap);
            if (null != crAmtMap && 0 < crAmtMap.size()) {
                    String crAmt = StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt"));
                    result = result.add(new BigDecimal(crAmt));
                }
        }
        return result;
    }

    @Override
    public LinkedList<Map<String, Object>> getItemCode(Map<String, Object> beanMap) {
        return reportMapper.getItemCode(beanMap);
    }

    @Override
    public List<ReportRsp> getItemCodeData(Map<String, Object> paramsMap) {
        return reportMapper.getItemCodeData(paramsMap);
    }

    @Override
    public String selectItemNameByItemCode(Map<String, Object> paramsMap) {
        return reportMapper.selectItemNameByItemCode(paramsMap);
    }


}
