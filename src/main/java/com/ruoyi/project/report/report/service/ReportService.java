package com.ruoyi.project.report.report.service;

import com.ruoyi.project.report.report.domain.ReportRsp;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/9/18 10:37
 */
public interface ReportService {

    /**
     *
     * @param paramsMap
     * @return
     */
    List<ReportRsp> getData(Map paramsMap);

    /**
     * 获取所有的bAccCode
     * @return
     */
    List<Map<String, Object>> getBAccCode(Map paramsMap);

    /**
     * 根据条件查询
     * @param paramMap
     * @return
     */
    Map<String, Object> getDateByCondition(Map<String, Object> paramMap);

    /**
     * 跟Acccode  获取信息
     * @param paramsMap
     * @return
     */
    BigDecimal getDataByAccCode(Map paramsMap);

    /**
     * 获取金额
     * @param paramMap
     * @return
     */
    BigDecimal getCRAmt(Map<String, Object> paramMap);

    /**
     * 获取item_code
     * @return
     */
    LinkedList<Map<String, Object>> getItemCode(Map<String, Object> beanMap);


    List<ReportRsp> getItemCodeData(Map<String, Object> paramsMap);

    String selectItemNameByItemCode( Map<String, Object> paramsMap);

    /**
     * 获取符合全表的数据
     * @param beanMap
     * @return
     */
    List<ReportRsp>  getAccCode(Map<String, Object> beanMap);

    /**
     * b报表3的数据
     * @param beanMap
     * @return
     */
    List<ReportRsp> getDataByCondition(Map<String, Object> beanMap);
}
