package com.ruoyi.project.report.Z03.domain;

import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * @program: timo
 * @description: 报表
 * @author: authorName
 * @create: 2020-09-11 21:29
 **/
@Data
public class Z03Report extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // private String bAccCode; //支出功能分类科目编码
    // private String bAccName; //科目名称
    // private String total;//本年收入合计
    // private String czbksr; //财政拨款收入
    // private String sjbzsr; //上级补助收入
    // private String xj;// 事业收入/小计
    // private String jysf;//其中:教育收费
    // private String jysr; //经营收入
    // private String fsdwsjsr;//附属单位上缴收入
    // private String qtsr;//其他收入
    private String deptName; //部门ID
}
