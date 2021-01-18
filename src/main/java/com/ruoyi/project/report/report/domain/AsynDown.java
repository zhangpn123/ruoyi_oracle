package com.ruoyi.project.report.report.domain;

import lombok.Data;


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

    private String status;//处理状态 1: 成功 2:失败 3:处理中

    private String msg;//处理 消息

    private String updateDate;//更新时间

    private String createDate; //创建时间

    private String finishDate; //完成时间

    private String deptId;//部门ID

    private String parentId;//父ID

    private String timeInterval;//统计时间的区间

}
