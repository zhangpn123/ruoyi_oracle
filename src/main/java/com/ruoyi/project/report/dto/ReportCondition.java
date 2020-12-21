package com.ruoyi.project.report.dto;

import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * @program:
 * @description: 报表
 * @author: authorName
 * @create: 2020-09-11 21:29
 **/
@Data
public class ReportCondition extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String unit;//展示单位
    private String deptName; //部门ID
    private String beginTime;//开始时间
    private String endTime;//结束时间
}
