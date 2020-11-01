package com.ruoyi.project.report.report.domain;

import lombok.Data;

import java.util.Date;

/**
 * @program: RuoYi
 * @description: 异步下载
 * @author: authorName
 * @create: 2020-11-01 10:41
 **/
@Data
public class AsynDown {

    private String id;

    private String filePath;//文件路径

    private String fileName;//文件名称

    private String status;//处理状态 1: 成功 2:失败

    private String msg;//处理 消息

    private Date createDate; //创建时间

}
