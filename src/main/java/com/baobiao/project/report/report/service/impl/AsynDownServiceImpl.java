package com.baobiao.project.report.report.service.impl;

import com.baobiao.common.utils.DateUtils;
import com.baobiao.common.utils.file.FileUtils;
import com.baobiao.common.utils.text.Convert;
import com.baobiao.framework.config.RuoYiConfig;
import com.baobiao.project.report.report.domain.AsynDown;
import com.baobiao.project.report.report.mapper.AsynDownMapper;
import com.baobiao.project.report.report.service.AsynDownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: RuoYi
 * @description: 异步下载
 * @author: authorName
 * @create: 2020-11-01 10:51
 **/
@Service
public class AsynDownServiceImpl implements AsynDownService {

    @Autowired
    private AsynDownMapper asynDownMapper;

    @Override
    public int saveFile(AsynDown asynDown) {
        asynDown.setParentId(asynDown.getDeptId());
        asynDown.setCreateDate(DateUtils.curDateTime());
        asynDown.setUpdateDate(DateUtils.curDateTime());
        asynDownMapper.save(asynDown);
        return 0;
    }

    @Override
    public List<AsynDown> selectAsynDownList(AsynDown asynDown) {
        return asynDownMapper.selectAsynDownList(asynDown);
    }

    @Override
    public AsynDown selectAsynDown(String id) {
        return asynDownMapper.selectAsynDown(id);
    }

    @Override
    public int deleteUserByIds(String ids) {
        String[] idArr = Convert.toStrArray(ids);
        //删除本地文件
        String rootPath = RuoYiConfig.getProfile();//根路径
        for (String id : idArr) {
            AsynDown asynDown = asynDownMapper.selectAsynDown(id);
            FileUtils.deleteFile(rootPath+asynDown.getFilePath()+asynDown.getFileName());
        }
        return asynDownMapper.deleteAsynDownByIds(idArr);
    }

    @Override
    public int updateFile(AsynDown asynDown) {
        asynDown.setUpdateDate(DateUtils.curDateTime());
        asynDown.setFinishDate(DateUtils.curDateTime());
        return asynDownMapper.updateFile(asynDown);
    }

    @Override
    public int deleteAll() {
        return asynDownMapper.deleteAll();
    }

    @Override
    public int update(AsynDown asynDown) {
        asynDown.setUpdateDate(DateUtils.curDateTime());
        asynDown.setCreateDate(DateUtils.curDateTime());
        return asynDownMapper.update(asynDown);
    }

    @Override
    public int countByStatus() {
        return asynDownMapper.countByStatus();
    }


}
