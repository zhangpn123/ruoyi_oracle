package com.ruoyi.project.report.job.mapper;

import com.ruoyi.project.report.job.domain.GlBal;

import java.util.List;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2021/1/15 15:02
 */
public interface CursorMapper {

    List<GlBal> selectByGlBal();

    List<GlBal> selectByGlPrebal();

}
