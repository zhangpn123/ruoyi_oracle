package com.ruoyi.project.report.Z05.service.impl;

import com.ruoyi.common.utils.FileUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.Z03.mapper.Z03Mapper;
import com.ruoyi.project.report.Z05.mapper.Z05Mapper;
import com.ruoyi.project.report.Z05.service.Z05Service;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2020-12-07 09:07
 **/
@Service
public class Z05ServiceImpl implements Z05Service {

    @Autowired
    private Z05Mapper z05Mapper;

    @Autowired
    private Z03Mapper z03Mapper;


    @Override
    public List<Map<String, Object>> selectRoleList(List<String> fieldList, List<Map<String, Object>> replaceMap, Z03Report z03Report) {
        Map paramsMap = new HashMap();
        List<Map<String, Object>> resultList = new LinkedList<>();
        /*获取bAccCode*/
        List<Map<String, Object>> bAccCodeList = z03Mapper.selBAccCode();
        if (null != bAccCodeList && 0 < bAccCodeList.size()) {
            for (Map<String, Object> map : bAccCodeList) {
                Map todoMap = new HashMap();
                for (int i = 0; i < fieldList.size(); i++) {
                    String fieldName = fieldList.get(i);
                    // if(fieldName.contains("bAccCode") || fieldName.contains("bAccName")){
                    if (fieldName.contains("bAccCode")) {
                        todoMap.put(fieldName, map.get(fieldName));
                    }else if (fieldName.contains("null")) {
                        todoMap.put(fieldName, "0.00");
                    } else if (fieldName.equalsIgnoreCase("sum")) {
                        todoMap.put("sum", "0.00");
                    } else if (fieldName.contains("sum")) {
                        todoMap.put(fieldName, "0.00");
                        String splitFirst = "";//是否是sum
                        String str = "";//获取()内的数据
                        if (fieldName.split("\\(").length > 1) {
                            splitFirst = fieldName.split("\\(")[0];
                            //说明是 ( 这个符号(英文)
                            //获取里面的值
                            int start = fieldName.indexOf("(") + 1;//截取开始值
                            int end = fieldName.indexOf(")");//截取结束值
                            str = fieldName.substring(start, end);//获取()内的数据
                        }

                        int size = 0;//小计长度
                        if (str.split(",").length > 1) {
                            size = StringUtils.getObjInt(str.split(",")[1]) - StringUtils.getObjInt(str.split(",")[0]) + 1;
                        }
                        size += i;//小计的长度

                        /**
                         * 小计这段的数据的操作
                         */
                        for (int k = i + 1; k < size + 1; k++) {
                            String accCode2 = fieldList.get(k);
                            if (accCode2.contains("null")) {
                                // todoMap.put(accCode2, map.get(fieldName));
                                todoMap.put(accCode2, "0.00");
                            }
                            paramsMap.clear();
                            paramsMap.put("accCode", accCode2);
                            paramsMap.put("bAccCode", map.get("bAccCode"));
                            paramsMap.put("deptName", z03Report.getDeptName());
                            Map<String, Object> resMap = z05Mapper.selectByCondition(paramsMap);
                            if (null != resMap && 0 < resMap.size()) {
                                String crAmt = StringUtils.getObjStrBigDeci(resMap.get("crAmt"));
                                // todoMap.put("bAccCode", map.get("bAccCode"));
                                todoMap.put(accCode2, new BigDecimal(crAmt).toString());
                                todoMap.put("sum", new BigDecimal(todoMap.get("sum").toString()).add(new BigDecimal(crAmt)).toString());
                                todoMap.put(fieldName, new BigDecimal(todoMap.get(fieldName).toString()).add(new BigDecimal(crAmt)).toString());
                            } else {
                                todoMap.put(accCode2, "0.00");
                            }
                        }
                        i = size;
                    } else {
                        paramsMap.clear();
                        paramsMap.put("accCode", fieldName);
                        paramsMap.put("bAccCode", map.get("bAccCode"));
                        paramsMap.put("deptName", z03Report.getDeptName());
                        Map<String, Object> resMap = z05Mapper.selectByCondition(paramsMap);
                        if (null != resMap && 0 < resMap.size()) {
                            String crAmt = StringUtils.getObjStrBigDeci(resMap.get("crAmt"));
                            // todoMap.put("bAccCode",map.get("bAccCode"));
                            todoMap.put(fieldName, new BigDecimal(crAmt).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                            todoMap.put("sum", new BigDecimal(todoMap.get("sum").toString()).add(new BigDecimal(crAmt)));
                        } else {
                            todoMap.put(fieldName, "0.00");
                        }
                    }
                }
                if(!StringUtils.getObjStrBigDeci(todoMap.get("sum")).equalsIgnoreCase("0.00")){
                    todoMap.remove("bAccName");
                    resultList.add(todoMap);
                }
            }
        }
        return resultList;
    }

}
