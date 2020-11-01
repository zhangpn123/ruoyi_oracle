package com.ruoyi.project.report.report.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.text.Convert;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.project.report.report.domain.AsynDown;
import com.ruoyi.project.report.report.mapper.AsynDownMapper;
import com.ruoyi.project.report.report.service.AsynDownService;
import com.ruoyi.project.system.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.nio.cs.ext.ISCII91;

import java.util.List;
import java.util.Map;

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
    public int saveFile(Map paramsMap) {
        String filePath = StringUtils.getObjStr(paramsMap.get("filePath"));
        String fileName = StringUtils.getObjStr(paramsMap.get("fileName"));
        String status = StringUtils.getObjStr(paramsMap.get("status"));
        String msg = StringUtils.getObjStr(paramsMap.get("msg"));
        if(StringUtils.isEmpty(filePath) || StringUtils.isEmpty(fileName) || StringUtils.isEmpty(status) || StringUtils.isEmpty(msg)){
            return -1;
        }
        AsynDown asynDown = new AsynDown();
        asynDown.setId(IdUtils.simpleUUID());
        asynDown.setFileName(fileName);
        asynDown.setFilePath(filePath);
        asynDown.setStatus(status);
        asynDown.setMsg(msg);

        // asynDownMapper.
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
}
