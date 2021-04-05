package com.ruoyi.project.report.Z04.controller;

import com.ruoyi.common.utils.PoiUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.Z03.service.Z03Service;
import com.ruoyi.project.report.dto.ReportCondition;
import com.ruoyi.project.system.dept.service.IDeptService;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @program: RuoYi
 * @description: Z03 收入决算表(财决03表)
 * @author: authorName
 * @create: 2020-09-18 10:32
 **/
@Controller
@RequestMapping("/report/Z04")
@Slf4j
public class Z04Controller extends BaseController {

    @Autowired
    private Z03Service z03Service;

    @Autowired
    private IDictTypeService dictTypeService;

    @Autowired
    private IDeptService iDeptService;

    @Autowired
    private IDictDataService dictDataService;

    private String prefix = "report/Z04";

    @RequiresPermissions("report:Z04:view")
    @GetMapping()
    public String Z04() {
        return prefix + "/Z04";
    }

    @RequiresPermissions("report:Z04:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ReportCondition reportCondition) {
        log.info("开始查询Z04报表数据");
        /*获取表头信息*/
        dictTypeService.clearCache();//清楚缓存
        List<DictData> dictData = dictTypeService.selectDictDataByType("report_z04_column");
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

        if (StringUtils.isEmpty(reportCondition.getDeptId())) {
            /*取当前用户的所属部门*/
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            reportCondition.setDeptId(user.getDeptId());
        }

        LinkedList<Map<String, Object>> list = z03Service.selectRoleList(fieldList, replaceMap, reportCondition);
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
    @RequiresPermissions("report:Z04:import")
    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
        List<DictData> dictDataList = util.importExcel(file.getInputStream());
        String message  = dictDataService.importDictData(dictDataList,updateSupport,"report_Z04_column");
        return AjaxResult.success(message);
    }

    /**
     * 导入模板
     * @return
     */
    @RequiresPermissions("report:Z04:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
        return util.importTemplateExcel("Z04报表模板");
    }

    // /**
    //  * 报表导出
    //  */
    // @Log(title = "报表统计", businessType = BusinessType.EXPORT)
    // @RequiresPermissions("report:Z04:export")
    // @RequestMapping("/export")
    // @ResponseBody
    // public AjaxResult exportReport(ReportCondition reportCondition) throws IOException {
    //     log.info("Start generating reports");
    //     //构建Excel
    //     String fileName = System.currentTimeMillis() + ".xls";
    //     String excelPath = RuoYiConfig.getDownloadPath() + fileName;
    //
    //
    //     //构建excel数据
    //     /*获取表头信息*/
    //     dictTypeService.clearCache();//清除缓存
    //     List<DictData> dictData = dictTypeService.selectDictDataByType("report_Z04_column");
    //     /*存储表头信息*/
    //     LinkedList<String> fieldList = new LinkedList<>();
    //     LinkedList<String> titleList = new LinkedList<>();
    //
    //     /*需要合并的数据信息*/
    //     List<Map<String, Object>> replaceMap = new ArrayList<>();
    //     Map<String, Object> map = new HashMap<>();
    //
    //     if (dictData != null && dictData.size() > 0) {
    //         for (DictData dictDatum : dictData) {
    //             String[] split = dictDatum.getDictValue().split("/");
    //             if (split.length > 1) {
    //                 /*如果长度大于1就是有需要合并的数据*/
    //                 map.put("dest", split[0]);//将要合并到的数据
    //
    //                 List srctList = new ArrayList();
    //                 for (int i = 1; i < split.length; i++) {
    //                     srctList.add(split[i]);
    //                 }
    //                 map.put("src", srctList);//需要合并的数据
    //                 replaceMap.add(map);
    //                 fieldList.add(split[0]);
    //             } else {
    //                 fieldList.add(split[0]);
    //             }
    //             titleList.add(dictDatum.getDictLabel());
    //         }
    //     }
    //
    //
    //     List<Map<String, Object>> resultList = Z04Service.selectRoleList(fieldList, replaceMap, reportCondition);
    //     // titleList.addFirst("项目");
    //     // titleList.addFirst("项目");
    //     // fieldList.addFirst("bAccCode");
    //     // fieldList.addFirst("bAccCode");
    //     //构建excel数据
    //     InputStream input = PoiUtil.getExcelFile(resultList, "Z03 收入决算表(财决03表)", titleList, fieldList);
    //     ExcelUtil.writeExcel(input, excelPath);
    //     return AjaxResult.success(fileName);
    // }


}
