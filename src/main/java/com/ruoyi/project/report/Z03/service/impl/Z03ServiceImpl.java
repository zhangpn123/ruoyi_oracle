package com.ruoyi.project.report.Z03.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.Z03.domain.Z03ReportRsp;
import com.ruoyi.project.report.Z03.mapper.Z03Mapper;
import com.ruoyi.project.report.Z03.service.Z03Service;
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
public class Z03ServiceImpl implements Z03Service {

    @Autowired
    private Z03Mapper z03Mapper;

    @Override
    public List<Map<String, Object>> selectRoleList(List<String> fieldList, List<Map<String, Object>> replaceMap, Z03Report z03Report) {
        Map paramsMap = new HashMap();
        List<Map<String, Object>> resultList = new LinkedList<>();

        /*获取bAccCode*/
        List<Map<String, Object>> bAccCodeList = z03Mapper.selBAccCode();

        /*初始化合计列*/
        Map totalMap = new HashMap();
        for (String s : fieldList) {
            totalMap.put(s, "0.00");//初始化为0.00;
        }
        resultList.add(0, totalMap);
        int index = 1;
        for (Map<String, Object> map : bAccCodeList) {
            String bAccCode = map.get("bAccCode").toString();
            paramsMap.clear();
            paramsMap.put("bAccCode", bAccCode);
            if (!StringUtils.isEmpty(z03Report.getDeptName())) {
                paramsMap.put("deptName", z03Report.getDeptName());
            }
            List<Map<String, Object>> list = z03Mapper.selectReport(paramsMap);
            Map<String, Object> data = getData(list, fieldList, replaceMap, 4);
            if (!StringUtils.getObjStr(data.get("total")).equals("0.00")) {
                Set<String> keySet = data.keySet();
                for (String key : keySet) {
                    totalMap.put(key, new BigDecimal(totalMap.get(key).toString()).setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(data.get(key).toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).toString());
                }
                data.put("bAccName","");
                resultList.add(index, data);
                index++;
            }
        }
        Map<String, Object> map = resultList.get(0);
        map.remove("bAccCode");
        map.put("bAccName","合计");
        return resultList;
    }

    @Override
    public List<Z03ReportRsp> getData( Map paramsMap) {
        return z03Mapper.getData(paramsMap);
    }

    /**
     * 数据转换
     *
     * @param paramsMapList 需要处理的数据
     * @param accCodeList   需要展示的field
     * @param replaceMap    需要合并的数据
     * @return
     */
    public Map<String, Object> getData(List<Map<String, Object>> paramsMapList, List<String> accCodeList, List<Map<String, Object>> replaceMap, int lenght) {
        /*需要返回的数据*/
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal total = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
        for (String accCode : accCodeList) {//设置初始值
            if (!accCode.equals("bAccName")) {
                resultMap.put(accCode, new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        /*校验遍历*/
        if (paramsMapList != null && paramsMapList.size() > 0) {
            for (Map<String, Object> paramsMap : paramsMapList) {
                /*校验遍历*/
                if (paramsMap != null && paramsMap.size() > 0) {
                    /*截取指定长度的值 进行比较*/
                    String accCode = paramsMap.get("accCode").toString().substring(0, lenght);
                    /*判断list中是否有需要的field*/
                    if (accCodeList.contains(accCode)) {
                        /*判断是否需要合并的数据*/
                        if (replaceMap != null && replaceMap.size() > 0) {
                            for (Map<String, Object> map : replaceMap) {
                                if (map != null && map.size() > 0) {
                                    List srctList = (List) map.get("src");
                                    for (Object o : srctList) {
                                        if (accCode.equals(o.toString())) {
                                            accCode = map.get("dest").toString();
                                        }
                                    }
                                }
                            }
                        }
                        BigDecimal oldCaAmt = new BigDecimal(resultMap.get(accCode).toString());//取出已存的金额
                        BigDecimal newCaAmt = new BigDecimal(StringUtils.getObjStrto0(paramsMap.get("caAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                        resultMap.put(accCode, oldCaAmt.add(newCaAmt).toString());//加上新的金额
                        total = total.add(newCaAmt);
                    }
                    resultMap.put("bAccCode", paramsMap.get("bAccCode"));
                }
            }
        }
        resultMap.put("total", total.toString());
        return resultMap;
    }

}
