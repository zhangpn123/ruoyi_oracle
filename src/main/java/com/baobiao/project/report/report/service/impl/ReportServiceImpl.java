package com.baobiao.project.report.report.service.impl;

import com.baobiao.common.utils.StringUtils;
import com.baobiao.framework.aspectj.lang.annotation.DataSource;
import com.baobiao.framework.aspectj.lang.enums.DataSourceType;
import com.baobiao.project.report.Z03.mapper.Z03Mapper;
import com.baobiao.project.report.report.domain.ReportRsp;
import com.baobiao.project.report.report.mapper.ReportMapper;
import com.baobiao.project.report.report.service.ReportService;
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
@DataSource(value = DataSourceType.SLAVE)
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
    public List<Map<String, Object>> getBAccCode(Map paramsMap) {
        return z03Mapper.selBAccCode(paramsMap);
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
                    BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(map.get("crAmt")));
                    if (resMap.containsKey(bAccCode+"/"+itemCode)) {
                        resMap.put(bAccCode+"/"+itemCode, new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode+"/"+itemCode))).add(crAmt).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        resMap.put(bAccCode+"/"+itemCode, crAmt);
                    }
                }
            }
        }else {
            List<Map<String, Object>> dateByCondition = reportMapper.getDateByCondition(paramMap);
            /*封装数据*/
            if (dateByCondition != null && dateByCondition.size() > 0) {
                for (Map<String, Object> map : dateByCondition) {
                    String bAccCode = StringUtils.getObjStr(map.get("bAccCode"));
                    BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(map.get("crAmt")));
                    if (resMap.containsKey(bAccCode)) {
                        resMap.put(bAccCode, new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode))).add(crAmt).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        resMap.put(bAccCode, crAmt);
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
                // if(split[i].startsWith("1") || split[i].startsWith("2") || split[i].startsWith("3")){
                //     paramsMap.put("beginTime","2020-00");
                // }
                Map<String, Object> resMap = reportMapper.getDataByAccCode(paramsMap);
                if (null != resMap && 0 < resMap.size()) {
                    BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(resMap.get("crAmt")));
                    BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(resMap.get("drAmt")));
                    switch (split[i].substring(0,1)){
                        case  "4" :  crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "6" :   crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "5" :   crAmt = drAmt.subtract(crAmt);
                            break;
                        case  "7" :   crAmt = drAmt.subtract(crAmt);
                            break;
                        // case "1":      crAmt = crAmt.subtract(drAmt);
                        //     break;
                        // case "2":      crAmt = drAmt.subtract(crAmt);
                        //     break;
                        // case "3":      crAmt = drAmt.subtract(crAmt);
                        //     break;
                    }
                     sum = sum.add(crAmt);
                    }
            }
        } else if (accCode.contains("-")) {
            String[] split = accCode.split("-");
            for (int i = 0; i < split.length; i++) {
                paramsMap.put("acc_code", split[i]);
                // if(split[i].startsWith("1") || split[i].startsWith("2") || split[i].startsWith("3")){
                //     paramsMap.put("beginTime","2020-00");
                // }
                Map<String, Object> resMap = reportMapper.getDataByAccCode(paramsMap);
                if (null != resMap && 0 < resMap.size()) {
                    BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(resMap.get("crAmt")));
                    BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(resMap.get("drAmt")));
                    switch (split[i].substring(0,1)){
                        case  "4" :  crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "6" :   crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "5" :   crAmt = drAmt.subtract(crAmt);
                            break;
                        case  "7" :   crAmt = drAmt.subtract(crAmt);
                            break;
                        // case "1":      crAmt = crAmt.subtract(drAmt);
                        //     break;
                        // case "2":      crAmt = drAmt.subtract(crAmt);
                        //     break;
                        // case "3":      crAmt = drAmt.subtract(crAmt);
                        //     break;
                    }
                        if (i == 0) {
                            sum = sum.add(crAmt);
                        }
                        sum = sum.subtract(crAmt);
                }
            }
        } else {
            // if(accCode.startsWith("1") || accCode.startsWith("2") || accCode.startsWith("3")){
            //     paramsMap.put("beginTime","2020-00");
            // }
           Map<String, Object> resMap = reportMapper.getDataByAccCode(paramsMap);
            if (null != resMap && 0 < resMap.size()) {
                BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(resMap.get("crAmt")));
                BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(resMap.get("drAmt")));
                switch (accCode.substring(0,1)){
                    case  "4" :  crAmt = crAmt.subtract(drAmt);
                        break;
                    case  "6" :   crAmt = crAmt.subtract(drAmt);
                        break;
                    case  "5" :   crAmt = drAmt.subtract(crAmt);
                        break;
                    case  "7" :   crAmt = drAmt.subtract(crAmt);
                        break;
                    // case "1":      crAmt = crAmt.subtract(drAmt);
                    //     break;
                    // case "2":      crAmt = drAmt.subtract(crAmt);
                    //     break;
                    // case "3":      crAmt = drAmt.subtract(crAmt);
                    //     break;
                }
                    sum = sum.add(crAmt);
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
                        // String crAmt = StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt"));
                    BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt")));
                    BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(crAmtMap.get("drAmt")));
                    switch (acc_code.substring(0,1)){
                        case  "4" :  crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "6" :   crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "5" :   crAmt = drAmt.subtract(crAmt);
                            break;
                        case  "7" :   crAmt = drAmt.subtract(crAmt);
                            break;
                    }
                        result = result.add(crAmt);
                }
            }
        } else if (acc_code.contains("-")) {
            String[] split = acc_code.split("-");
            for (int i = 0; i < split.length; i++) {
                paramMap.put("acc_code", split[i]);
                Map<String, Object> crAmtMap = reportMapper.getCRAmt(paramMap);
                if (null != crAmtMap && 0 < crAmtMap.size()) {
                    BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt")));
                    BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(crAmtMap.get("drAmt")));
                    switch (acc_code.substring(0,1)){
                        case  "4" :  crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "6" :   crAmt = crAmt.subtract(drAmt);
                            break;
                        case  "5" :   crAmt = drAmt.subtract(crAmt);
                            break;
                        case  "7" :   crAmt = drAmt.subtract(crAmt);
                            break;
                    }
                        if (i == 0) {
                            result = result.add(crAmt);
                        }
                        result = result.subtract(crAmt);
                }
            }
        } else {
            Map<String, Object> crAmtMap = reportMapper.getCRAmt(paramMap);
            if (null != crAmtMap && 0 < crAmtMap.size()) {
                BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(crAmtMap.get("crAmt")));
                BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(crAmtMap.get("drAmt")));
                switch (acc_code.substring(0,1)){
                    case  "4" :  crAmt = crAmt.subtract(drAmt);
                        break;
                    case  "6" :   crAmt = crAmt.subtract(drAmt);
                        break;
                    case  "5" :   crAmt = drAmt.subtract(crAmt);
                        break;
                    case  "7" :   crAmt = drAmt.subtract(crAmt);
                        break;
                }
                    result = result.add(crAmt);
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

    @Override
    public List<ReportRsp>  getAccCode(Map<String, Object> beanMap) {
        // List<ReportRsp> reportRspList = reportMapper.getAccCode(beanMap);
        // for (ReportRsp reportRsp : reportRspList) {
        //     String accCode = reportRsp.getAccCode();
        //     BigDecimal crAmt = new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getCrAmt()));
        //     BigDecimal drAmt = new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getDrAmt()));
        //     BigDecimal amt = new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt()));
        //     switch (accCode.substring(0,1)){
        //         case  "4" :  amt = crAmt.subtract(drAmt);
        //             break;
        //         case  "6" :   amt = crAmt.subtract(drAmt);
        //             break;
        //         case  "5" :   amt = drAmt.subtract(crAmt);
        //             break;
        //         case  "7" :   amt = drAmt.subtract(crAmt);
        //             break;
        //     }
        //     reportRsp.setAmt(amt.toString());
        // }
        return reportMapper.getAccCode(beanMap);
    }

    @Override
    public List<ReportRsp> getDataByCondition(Map<String, Object> beanMap) {
        return reportMapper.getDataByCondition(beanMap);
    }


}
