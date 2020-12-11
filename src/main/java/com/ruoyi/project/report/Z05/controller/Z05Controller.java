package com.ruoyi.project.report.Z05.controller;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.Z05.service.Z05Service;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2020-12-07 09:07
 **/
@Controller
@RequestMapping("/report/Z05")
@Slf4j
public class Z05Controller extends BaseController {
    @Autowired
    private Z05Service z05Service;

    @Autowired
    private IDictTypeService dictTypeService;

    @Autowired
    private IDictDataService dictDataService;

    private String prefix = "report/Z05";

    @RequiresPermissions("report:Z05:view")
    @GetMapping()
    public String z05() {
        return prefix + "/Z05";
    }

    @RequiresPermissions("report:Z05:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Z03Report z03Report) {
        log.info("开始查询Z05报表数据");

        /*获取表头信息*/
        dictTypeService.clearCache();//清楚缓存
        List<DictData> dictData = dictTypeService.selectDictDataByType("report_z05_column");
        /*存储表头信息*/
        List<String> fieldList = new LinkedList<>();

        /*需要合并的数据信息*/
        List<Map<String, Object>> replaceMap = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        if (dictData != null && dictData.size() > 0) {
            for (DictData dictDatum : dictData) {
                String[] split = dictDatum.getDictValue().split("/");
                if (split.length > 1) {
                    /*如果长度大于1就是有需要合并的数据*/
                    map.put("dest", split[0]);//将要合并到的数据
                    //需要合并的数据
                    List srctList = new ArrayList();
                    for (int i = 1; i < split.length; i++) {
                        srctList.add(split[i]);
                        fieldList.add(split[i]);
                    }
                    map.put("src", srctList);
                    replaceMap.add(map);
                    fieldList.add(split[0]);
                } else {
                    fieldList.add(split[0]);
                }
            }
        }

         List<Map<String, Object>> list = z05Service.selectRoleList(fieldList, replaceMap, z03Report);
        return getDataTable(list);
    }

    /**
     * 报表导入
     * @param file
     * @param updateSupport
     * @return
     * @throws Exception
     */
    @Log(title = "报表统计", businessType = BusinessType.IMPORT)
    @RequiresPermissions("report:Z05:import")
    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
        List<DictData> dictDataList = util.importExcel(file.getInputStream());
        String message  = dictDataService.importDictData(dictDataList,updateSupport,"report_z05_column");
        return AjaxResult.success(message);
    }

    /**
     * 导入模板
     * @return
     */
    @RequiresPermissions("report:Z05:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
        return util.importTemplateExcel("Z05报表模板");
    }
}
