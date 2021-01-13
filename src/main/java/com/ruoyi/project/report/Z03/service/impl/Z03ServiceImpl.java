package com.ruoyi.project.report.Z03.service.impl;

import com.ruoyi.common.constant.Constans;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.Map2Bean;
import com.ruoyi.project.report.dto.ReportCondition;
import com.ruoyi.project.report.Z03.mapper.Z03Mapper;
import com.ruoyi.project.report.Z03.service.Z03Service;
import org.apache.commons.beanutils.BeanUtilsBean2;
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
    public LinkedList<Map<String, Object>> selectRoleList(List<String> fieldList, List<Map<String, Object>> replaceMap, ReportCondition reportCondition) {
        Map paramsMap = new HashMap();
        LinkedList<Map<String, Object>> resultList = new LinkedList<>();

        /*获取bAccCode*/
        LinkedList<Map<String, Object>> bAccCodeList = z03Mapper.selBAccCode();

        /*初始化合计列*/
        Map<String,Object> totalMap = new HashMap();
        for (String s : fieldList) {
            totalMap.put(s, "0.00");//初始化为0.00;
        }
        resultList.add(0, totalMap);
        int index = 1;
        for (Map<String, Object> map : bAccCodeList) {
            String bAccCode = map.get("bAccCode").toString();
            paramsMap.clear();
            /*设置默认的日期时间*/
            Date date = new Date();
            if (StringUtils.isEmpty(reportCondition.getBeginTime())) {
                reportCondition.setBeginTime(DateUtils.getCntDtStr(date,"yyyy")+"-01");
            }
            if (StringUtils.isEmpty(reportCondition.getEndTime())) {
                reportCondition.setEndTime(DateUtils.getCntDtStr(date,"yyyy-MM"));
            }
            /*将实体类装成map*/
            paramsMap = Map2Bean.transBean2Map(reportCondition);
            paramsMap.put("bAccCode", bAccCode);

            List<Map<String, Object>> list = z03Mapper.selectReport(paramsMap);
            Map<String, Object> data = getData(list, fieldList, replaceMap);
            if (!StringUtils.getObjStrBigDeci(data.get("total")).equals("0.00")) {
                Set<String> keySet = data.keySet();
                for (String key : keySet) {
                    totalMap.put(key, new BigDecimal(StringUtils.getObjStrBigDeci(totalMap.get(key))).add(new BigDecimal(StringUtils.getObjStrBigDeci(data.get(key))).setScale(2, BigDecimal.ROUND_HALF_UP)).toString());
                }

                /*判断数据是否要转成万元格式*/
                if(reportCondition.getUnit().equalsIgnoreCase(Constans.Unit.UNIT_WANYUAN.getValue())){
                    for (Map.Entry<String, Object> stringObjectEntry : data.entrySet()) {
                        String temp = StringUtils.getObjStrBigDeci(stringObjectEntry.getValue());
                        if (!temp.equalsIgnoreCase("0.00")){
                            BigDecimal wanyun = new BigDecimal(temp).divide(new BigDecimal("10000")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                            data.put(stringObjectEntry.getKey(),wanyun.toString());
                        }
                    }
                }
                data.put("bAccName",map.get("bAccName"));
                data.put("bAccCode",map.get("bAccCode"));
                resultList.add(index, data);
                index++;
            }
        }
        Map<String, Object> map = resultList.get(0);
        map.remove("bAccCode");
        /*判断数据是否要转成万元格式*/
        if(reportCondition.getUnit().equalsIgnoreCase(Constans.Unit.UNIT_WANYUAN.getValue())) {
            for (Map.Entry<String, Object> o : map.entrySet()) {
                String temp = StringUtils.getObjStrBigDeci(o.getValue());
                if (!temp.equalsIgnoreCase("0.00")) {
                    BigDecimal wanyun = new BigDecimal(temp).divide(new BigDecimal("10000")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    map.put(o.getKey(), wanyun.toString());
                }
            }
        }
        map.put("bAccName","合计");
        return resultList;
    }


    /**
     * 数据转换
     *
     * @param paramsMapList 需要处理的数据
     * @param accCodeList   需要展示的field
     * @param replaceMap    需要合并的数据
     * @return
     */
    public Map<String, Object> getData(List<Map<String, Object>> paramsMapList, List<String> accCodeList, List<Map<String, Object>> replaceMap) {
        /*需要返回的数据*/
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal total = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
        for (String accCode : accCodeList) {//设置初始值
            if (!"bAccName".equalsIgnoreCase(accCode.trim()) && !"bAccCode".equalsIgnoreCase(accCode.trim())) {
                resultMap.put(accCode.trim(), new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            }
        }
        /*校验遍历*/
        if (paramsMapList != null && paramsMapList.size() > 0) {
            for (Map<String, Object> paramsMap : paramsMapList) {
                /*校验遍历*/
                if (paramsMap != null && paramsMap.size() > 0) {
                    /*截取指定长度的值 进行比较*/

                    for (String tempAccCode : accCodeList) {
                        if(paramsMap.get("accCode").toString().length() < tempAccCode.length()){
                            continue;
                        }
                         String accCode = paramsMap.get("accCode").toString().substring(0, tempAccCode.length());
                        /*判断list中是否有需要的field*/
                        if (tempAccCode.equalsIgnoreCase(accCode)) {
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
                    }

                    // resultMap.put("bAccCode", paramsMap.get("bAccCode"));
                }
            }
        }
        resultMap.put("total", total.toString());
        return resultMap;
    }

}
