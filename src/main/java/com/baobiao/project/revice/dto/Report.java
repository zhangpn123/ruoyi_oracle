package com.baobiao.project.revice.dto;

import lombok.Data;

/**
 * @program: RuoYi
 * @description: 统计表实体类
 * @author: authorName
 * @create: 2021-03-11 09:34
 **/
@Data
public class Report {
    private String coCode;
    private String coName;
    private String queType;
    private String queName;
    private String queNum;
    private String queDesc;
    private String voucherNo;
    private String remark;
    private String record;
    private String assCheck;
    private String createDate;
    private String reportDate;
}
