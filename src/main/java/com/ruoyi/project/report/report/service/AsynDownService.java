package com.ruoyi.project.report.report.service;

import com.ruoyi.project.report.report.domain.AsynDown;

import java.util.List;
import java.util.Map;

/**
 * @About:
 * @Author: zhangpuning
 * @Date: 2020/11/1 10:51
 */
public interface AsynDownService {
    /**
     *保存下载文件
     * @return
     */
    int saveFile(AsynDown asynDown);

    /**
     * 分页查询
     * @param asynDown
     * @return
     */
    List<AsynDown> selectAsynDownList(AsynDown asynDown);

    /**
     * 根据id查询对象
     * @param id
     * @return
     */
    AsynDown selectAsynDown(String id);

    /**
     * 删除
     * @param ids
     * @return
     */
    int deleteUserByIds(String ids);

    /**
     * 更新
     * @param asynDown
     * @return
     */
    int updateFile(AsynDown asynDown);
}
