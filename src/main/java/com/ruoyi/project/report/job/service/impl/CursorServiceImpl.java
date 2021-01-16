package com.ruoyi.project.report.job.service.impl;

import com.ruoyi.project.report.job.domain.GlBal;
import com.ruoyi.project.report.job.mapper.CursorMapper;
import com.ruoyi.project.report.job.service.CursorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: RuoYi
 * @description: 临时表处理
 * @author: authorName
 * @create: 2021-01-15 15:01
 **/
@Service
public class CursorServiceImpl implements CursorService {

    @Autowired
    private CursorMapper cursorMapper;

    @Override
    public List<GlBal> selectByCondition() {
        return cursorMapper.selectByGlBal();
    }

    // @Override
    // public int synchro(Cursor cursor) {
    //     /*删除符合条件的数据*/
    //     /*将临时表的数据新增到表里*/
    //     /*将临时表的数据删除*/
    //
    //     return 0;
    // }
}
