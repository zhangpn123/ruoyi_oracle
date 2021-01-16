package com.ruoyi.project.report.job.domain;

import lombok.Data;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-01-15 23:14
 **/
@Data
public class GlBal {
    private String balUniq;
    private String accountId;
    private String coCode;
    private String fiscal;
    private String fisPerd;
    private String accCode;
    private String accType;
    private String curCode;
    private String drAmt;
    private String crAmt;
    private String curdrAmt;
    private String curcrAmt;
    private String qtydrAmt;
    private String qtycrAmt;
    private String accItem1;
    private String accItem2;
    private String accItem3;
    private String accItem4;
    private String accItem5;
    private String accItem6;
    private String accItem7;
    private String accItem8;
    private String accItem9;
    private String accItem10;
    private String accItem11;
    private String accItem12;
    private String accItem13;
    private String accItem14;
    private String accItem15;
    private String accItem16;
    private String bCoCode;
    private String bAccCode;
    private String temp;
    private String itemCode;
    private String outlayCode;
    private String itemIsn;
    private String cashflowCode;
    private String vouKind;
    private String relation;//是否关联表 1关联GL_PREBAL  2不关联;
}
