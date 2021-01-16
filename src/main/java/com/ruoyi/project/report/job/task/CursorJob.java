package com.ruoyi.project.report.job.task;

import com.ruoyi.project.report.job.domain.GlBal;
import com.ruoyi.project.report.job.service.CursorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: RuoYi
 * @description: 定时处理临时表
 * @author: authorName
 * @create: 2021-01-15 14:58
 **/
@Component
@Slf4j
public class CursorJob {

    @Autowired
    private CursorService cursorService;

    // @Scheduled(cron = "0 */1 * * * ?") //5分钟执行一次
    public void execute() {
        /*获取符合条件的临时表做处理*/
        List<GlBal> resList = cursorService.selectByCondition();


    }

}
