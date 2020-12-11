package com.ruoyi.project.system.dict.service;

import java.util.List;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.system.dict.domain.DictType;
import com.ruoyi.project.system.user.domain.User;
import com.ruoyi.project.system.user.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.common.utils.text.Convert;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.mapper.DictDataMapper;
import com.ruoyi.project.system.dict.utils.DictUtils;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class DictDataServiceImpl implements IDictDataService {
    private static final Logger log = LoggerFactory.getLogger(DictDataServiceImpl.class);
    @Autowired
    private DictDataMapper dictDataMapper;

    @Override
    public String importDictData(List<DictData> dictDataList, boolean updateSupport,String dictName) {
        if (StringUtils.isNull(dictDataList) || dictDataList.size() == 0) {
            throw new BusinessException("导入的模板数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
            // if(updateSupport){
        int del = dictDataMapper.deleteDictDataByDictType(dictName);
            // }
        //导入成功之前吧数据删除
        for (DictData dictData : dictDataList) {
            try {
                dictData.setStatus("0");
                dictData.setIsDefault("Y");
                dictData.setDictType(dictName);
                dictData.setCreateBy(ShiroUtils.getLoginName());
                dictData.setDictValue(dictData.getDictValue().trim());
                int row = dictDataMapper.insertDictData(dictData);
                if (row > 0) {
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、列名：" + dictData.getDictLabel() + "导入成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、列名：" + dictData.getDictLabel() + "导入失败");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、列名：" + dictData.getDictLabel() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public DictData selectByDictLaber(DictData dictData) {
        return dictDataMapper.selectByDictLabel(dictData);
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<DictData> selectDictDataList(DictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public DictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据
     *
     * @param ids 需要删除的数据
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(String ids) {
        int row = dictDataMapper.deleteDictDataByIds(Convert.toStrArray(ids));
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(DictData dictData) {
        dictData.setCreateBy(ShiroUtils.getLoginName());
        int row = dictDataMapper.insertDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(DictData dictData) {
        dictData.setUpdateBy(ShiroUtils.getLoginName());
        int row = dictDataMapper.updateDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }


}
