package com.ruoyi.project.report.Z03.controller;

import com.ruoyi.common.utils.PoiUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.UploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.Z03.domain.Z03Report;
import com.ruoyi.project.report.Z03.domain.Z03ReportRsp;
import com.ruoyi.project.report.Z03.service.Z03Service;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.domain.DictType;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
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
     * 报表模板导入
     *
     * @param file
     * @param updateSupport
     * @return
     * @throws Exception
     */
    @Log(title = "报表统计", businessType = BusinessType.IMPORT)
    @RequiresPermissions("report:Z03:import")
    @PostMapping("/importExportTemp")
    @ResponseBody
    public AjaxResult importExportTemp(MultipartFile file, boolean updateSupport) throws Exception {
        //将下载模板存放到本地
        String fileName = "Z03ReportTemp.xls";
        String exportTempPath = RuoYiConfig.getZ03ReportPath() + fileName;
        FileUtils.deleteFile(exportTempPath);//删除之前的

        //保存新的到本地
        Boolean result = UploadUtils.upLoadFile(file, exportTempPath);
        if (result) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error();
        }

    }

    /**
     * 页面展示报表导入
     *
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
        String message = dictDataService.importDictData(dictDataList, updateSupport);
        return AjaxResult.success(message);
    }

    /**
     * 导入模板
     *
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
    public AjaxResult exportReport(Z03Report z03Report) {
        String tempFileName = "Z03ReportTemp.xls";
        String exportTempPath = RuoYiConfig.getZ03ReportPath() + tempFileName;
        try (FileInputStream fis = new FileInputStream(exportTempPath)) {

            log.info("Start generating reports");
            //构建Excel
            // String fileName = System.currentTimeMillis() + ".xls";
            String excelPath = RuoYiConfig.getDownloadPath() + tempFileName;

            //读取本地的导出模板
            HSSFWorkbook workBook = new HSSFWorkbook(fis);
            //获取对应的sheet
            HSSFSheet sheet = workBook.getSheet("Z03 收入决算表(财决03表)");

            /*遍历表中的行的*/
            for (int i = 9; i < sheet.getLastRowNum(); i++) {
                if (sheet.getRow(i) == null) {
                    //空行
                    continue;
                }
                BigDecimal sum = new BigDecimal("0.00");

                /*获取baccCode*/
                String bAccCode = ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(0));
                if (StringUtils.isEmpty(bAccCode)) {
                    continue;
                }
                Map paramsMap = new HashMap();
                paramsMap.put("bAccCode", bAccCode);
                paramsMap.put("deptName", z03Report.getDeptName());
                List<Z03ReportRsp> dataList = z03Service.getData(paramsMap);

                /*遍历表中的列*/
                // for (int j = 0; j <sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                for (int j = sheet.getRow(i).getPhysicalNumberOfCells() - 1; j > 3; j--) {

                    BigDecimal bigDecimal = new BigDecimal("0.00"); //统计符合列的数据

                    HSSFCell cell = sheet.getRow(i).getCell(j);
                    String cellVal = ExcelUtil.getStringValueFromCell(cell);

                    if (j == 4) {
                        cell.setCellValue(sum.toString());
                    } else {
                        /*值为空则设置为0*/
                        if (StringUtils.isEmpty(cellVal)) {
                            cell.setCellValue(bigDecimal.toString());
                        } else {

                            if (dataList != null && dataList.size() > 0) {
                                String[] split = cellVal.split("\\+");
                                for (int k = 0; k < split.length; k++) {
                                    for (Z03ReportRsp z03ReportRsp : dataList) {
                                        String accCode = z03ReportRsp.getAccCode();
                                        if (accCode.length() >= split[k].length()) {
                                            if (split[k].equalsIgnoreCase(accCode.substring(0, split[k].length()))) {
                                                bigDecimal = bigDecimal.add(new BigDecimal(z03ReportRsp.getCaAmt()));
                                            }
                                        }
                                    }
                                }
                                cell.setCellValue(bigDecimal.toString());
                            } else {
                                cell.setCellValue(bigDecimal.toString());
                            }
                            sum = sum.add(bigDecimal);
                        }
                    }

                }
                try (FileOutputStream out = new FileOutputStream(excelPath)) {
                    workBook.write(out); //写回数据
                }
            }
            return AjaxResult.success(tempFileName);
        } catch (Exception e) {
            log.error("导出数据失败!", e);
        }
        return AjaxResult.error();
    }

}
