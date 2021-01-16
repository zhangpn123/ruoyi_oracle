package com.ruoyi.project.report.job.service;

import com.ruoyi.project.report.job.domain.GlBal;

import java.util.List;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2021/1/15 15:01
 */
public interface CursorService {
    /**
     * 根据条件查询
     * @return
     */
    List<GlBal> selectByCondition();


}
