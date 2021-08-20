package com.baobiao.project.report.customquert.service.impl;

import com.baobiao.framework.aspectj.lang.annotation.DataSource;
import com.baobiao.framework.aspectj.lang.enums.DataSourceType;
import com.baobiao.project.report.customquert.mapper.CustomqueryMapper;
import com.baobiao.project.report.customquert.service.CustomqueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: RuoYi
 * @description: 自定义查询
 * @author: authorName
 * @create: 2020-12-29 13:54
 **/
@Service
@DataSource(value = DataSourceType.SLAVE)
public class CustomqueryServiceImpl implements CustomqueryService {

    @Autowired
    private CustomqueryMapper customqueryMapper;

    @Override
    public List selectList(String customSql) {
        return customqueryMapper.selectList(customSql);
    }
}
