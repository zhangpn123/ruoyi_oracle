package com.ruoyi.project.report.report.mapper;

import com.ruoyi.project.report.report.domain.AsynDown;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/11/1 17:59
 */
public interface AsynDownMapper {

    int  save(AsynDown asynDown);

    /**
     * 分页查询
     * @param asynDown
     * @return
     */
    List<AsynDown> selectAsynDownList(AsynDown asynDown);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    AsynDown selectAsynDown(@Param("id") String id);

    int deleteAsynDownByIds(String[] idArr);

    /**
     * 更新
     * @param asynDown
     * @return
     */
    int updateFile(AsynDown asynDown);

    int deleteAll();


    int update(AsynDown asynDown);

    int countByStatus();

}
