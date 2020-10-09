package com.ruoyi.project.report.Z03.controller;

import com.ruoyi.common.utils.PoiUtil;
import com.ruoyi.common.utils.UploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.Z03.service.Z03Service;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.domain.DictType;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.CRC32;

/**
 * @program: RuoYi
 * @description: Z03 收入决算表(财决03表)
 * @author: authorName
 * @create: 2020-09-18 10:32
 **/
@Controller
@RequestMapping("/report/Z03")
@Slf4j
public class Z03Controller extends BaseController {

    @Autowired
    private Z03Service z03Service;

    @Autowired
    private IDictTypeService dictTypeService;


    @Autowired
    private IDictDataService dictDataService;

    private String prefix = "report/Z03";

    @RequiresPermissions("report:Z03:view")
    @GetMapping()
    public String z03() {
        return prefix + "/Z03";
    }

    @RequiresPermissions("report:Z03:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Z03Report z03Report) {
        log.info("开始查询Z03报表数据");

        /*获取表头信息*/
        dictTypeService.clearCache();//清楚缓存
        List<DictData> dictData = dictTypeService.selectDictDataByType("report_z03_column");
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
                    }
                    map.put("src", srctList);
                    replaceMap.add(map);
                    fieldList.add(split[0]);
                } else {
                    fieldList.add(split[0]);
                }
            }
        }

        List<Map<String, Object>> list = z03Service.selectRoleList(fieldList, replaceMap, z03Report);
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
    @RequiresPermissions("report:Z03:import")
    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
        List<DictData> dictDataList = util.importExcel(file.getInputStream());
        String message  = dictDataService.importDictData(dictDataList,updateSupport);
        return AjaxResult.success(message);
    }

    /**
     * 导入模板
     * @return
     */
    @RequiresPermissions("report:Z03:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
        return util.importTemplateExcel("Z03报表模板");
    }

    /**
     * 报表导出
     */
    @Log(title = "报表统计", businessType = BusinessType.EXPORT)
    @RequiresPermissions("report:Z03:export")
    @RequestMapping("/export")
    @ResponseBody
    public AjaxResult exportReport(Z03Report z03Report) throws IOException {
        log.info("Start generating reports");
        //构建Excel
        String fileName = System.currentTimeMillis() + ".xls";
        String excelPath = RuoYiConfig.getDownloadPath() + fileName;


        //构建excel数据
        /*获取表头信息*/
        dictTypeService.clearCache();//清除缓存
        List<DictData> dictData = dictTypeService.selectDictDataByType("report_z03_column");
        /*存储表头信息*/
        LinkedList<String> fieldList = new LinkedList<>();
        LinkedList<String> titleList = new LinkedList<>();

        /*需要合并的数据信息*/
        List<Map<String, Object>> replaceMap = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        if (dictData != null && dictData.size() > 0) {
            for (DictData dictDatum : dictData) {
                String[] split = dictDatum.getDictValue().split("/");
                if (split.length > 1) {
                    /*如果长度大于1就是有需要合并的数据*/
                    map.put("dest", split[0]);//将要合并到的数据

                    List srctList = new ArrayList();
                    for (int i = 1; i < split.length; i++) {
                        srctList.add(split[i]);
                    }
                    map.put("src", srctList);//需要合并的数据
                    replaceMap.add(map);
                    fieldList.add(split[0]);
                } else {
                    fieldList.add(split[0]);
                }
                titleList.add(dictDatum.getDictLabel());
            }
        }


        List<Map<String, Object>> resultList = z03Service.selectRoleList(fieldList, replaceMap, z03Report);
        // titleList.addFirst("项目");
        // titleList.addFirst("项目");
        // fieldList.addFirst("bAccCode");
        // fieldList.addFirst("bAccCode");
        //构建excel数据
        InputStream input = PoiUtil.getExcelFile(resultList, "Z03 收入决算表(财决03表)", titleList, fieldList);
        ExcelUtil.writeExcel(input, excelPath);
        return AjaxResult.success(fileName);
    }


}
