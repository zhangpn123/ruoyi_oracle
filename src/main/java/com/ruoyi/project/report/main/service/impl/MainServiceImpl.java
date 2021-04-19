package com.ruoyi.project.report.main.service.impl;

import com.ruoyi.common.constant.Constans;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.Map2Bean;
import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.report.dto.ReportCondition;
import com.ruoyi.project.report.dto.ResponseMain;
import com.ruoyi.project.report.main.mapper.MainMapper;
import com.ruoyi.project.report.main.service.MainService;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.dept.service.IDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @program: ruoyi_oracle
 * @description:
 * @author: authorName
 * @create: 2021-04-04 22:03
 **/
@Service
@DataSource(value = DataSourceType.SLAVE)
public class MainServiceImpl implements MainService {


    @Autowired
    private MainMapper mainMapper;

    @Override
    public List<ResponseMain> selectData(List<String> fieldList, List<Map<String, Object>> replaceMap, ReportCondition reportCondition) {
        /*设置默认的日期时间*/
        Date date = new Date();
        if (StringUtils.isEmpty(reportCondition.getBeginTime())) {
            reportCondition.setBeginTime(DateUtils.getCntDtStr(date,"yyyy")+"-01");
        }
        if (StringUtils.isEmpty(reportCondition.getEndTime())) {
            reportCondition.setEndTime(DateUtils.getCntDtStr(date,"yyyy-MM"));
        }
        /*bean转成map*/
        Map<String, Object> beanMap = Map2Bean.transBean2Map(reportCondition);
        List<Map<String, Object>> list = mainMapper.selectReport(beanMap);
        return getData(list, fieldList, replaceMap);
    }

    @Override
    public List<Map<String, Object>> selectDataDetail(List<String> fieldList, List<Map<String, Object>> replaceMap, ReportCondition reportCondition) {
        /*设置默认的日期时间*/
        Date date = new Date();
        if (StringUtils.isEmpty(reportCondition.getBeginTime())) {
            reportCondition.setBeginTime(DateUtils.getCntDtStr(date,"yyyy")+"-01");
        }
        if (StringUtils.isEmpty(reportCondition.getEndTime())) {
            reportCondition.setEndTime(DateUtils.getCntDtStr(date,"yyyy-MM"));
        }

        /*bean转成map*/
        Map<String, Object> beanMap = Map2Bean.transBean2Map(reportCondition);
        return mainMapper.selectDataDetail(beanMap);
    }

    /**
     * 数据转换
     *
     * @param paramsMapList 需要处理的数据
     * @param accCodeList   需要展示的field
     * @param replaceMap    需要合并的数据
     * @return
     */
    public List<ResponseMain> getData(List<Map<String, Object>> paramsMapList, List<String> accCodeList, List<Map<String, Object>> replaceMap) {
        List<ResponseMain> responseMains = new ArrayList<>();
        /*需要返回的数据*/
        Map<String, Object> resultMap = new HashMap<>();
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
                            BigDecimal oldCrAmt = new BigDecimal(resultMap.get(accCode).toString());//取出已存的金额
                            BigDecimal newCrAmt = new BigDecimal(StringUtils.getObjStrto0(paramsMap.get("crAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                            resultMap.put(accCode, oldCrAmt.add(newCrAmt).toString());//加上新的金额
                        }

                    }
                }
            }
        }

        for (Map.Entry<String, Object> stringObjectEntry : resultMap.entrySet()) {
            ResponseMain responseMain = new ResponseMain();
            responseMain.setCode(stringObjectEntry.getKey());
            responseMain.setValue(stringObjectEntry.getValue().toString());
            responseMains.add(responseMain);
        }

        return responseMains;
    }

}
