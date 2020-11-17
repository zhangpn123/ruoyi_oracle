package com.ruoyi.project.report.report.controller;

import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.project.report.report.domain.AsynDown;
import com.ruoyi.project.report.report.domain.ReportRsp;
import com.ruoyi.project.report.report.service.AsynDownService;
import com.ruoyi.project.report.report.service.ReportService;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: RuoYi
 * @description: Z03 收入决算表(财决03表)
 * @author: authorName
 * @create: 2020-09-18 10:32
 **/
@Controller
@RequestMapping("/report")
@Slf4j
public class ReportController extends BaseController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AsynDownService asynDownService;

    private String prefix = "report";

    @RequiresPermissions("report:view")
    @GetMapping()
    public String report() {
        return prefix + "/report";
    }

    @RequiresPermissions("report:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AsynDown asynDown) {
        log.info("开始查询导出报表数据");
        startPage();
        List<AsynDown> list = asynDownService.selectAsynDownList(asynDown);
        return getDataTable(list);
    }


    /**
     * 报表模板导入
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Log(title = "报表统计", businessType = BusinessType.IMPORT)
    @RequiresPermissions("report:import")
    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importExportTemp(MultipartFile file) {
        //将下载模板存放到本地
        String fileName = "ReportTemp.xls";
        String exportTempPath = RuoYiConfig.getReportPath() + fileName;
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
     * 报表导出
     */
    @Log(title = "报表统计", businessType = BusinessType.EXPORT)
    @GetMapping("/downFile")
    public void fileDownload(String id, HttpServletResponse response, HttpServletRequest request) {
        try {
            AsynDown asynDown = asynDownService.selectAsynDown(id);
            String filePath = RuoYiConfig.getProfile() + asynDown.getFilePath() + asynDown.getFileName();//根路径
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;fileName="+java.net.URLEncoder.encode(asynDown.getFileName(),"UTF-8"));
            FileUtils.setAttachmentResponseHeader(response, asynDown.getFileName());
            FileUtils.writeBytes(filePath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequiresPermissions("report:remove")
    @Log(title = "报表统计", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        try {
            return toAjax(asynDownService.deleteUserByIds(ids));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 报表导出
     */
    @Log(title = "报表统计", businessType = BusinessType.EXPORT)
    @RequiresPermissions("report:export")
    @RequestMapping("/export")
    @ResponseBody
    public AjaxResult exportReport(Z03Report z03Report) {
        String tempFileName = "ReportTemp.xls";
        String exportTempPath = RuoYiConfig.getReportPath() + tempFileName;

        /*保存到本地的信息*/
        String fileName = System.currentTimeMillis() + ".xls";
        String rootPath = RuoYiConfig.getProfile();//根路径
        String filePath = RuoYiConfig.getAsynDown();
        String excelPath = rootPath + filePath + fileName;
        Map params = new HashMap();
        params.put("filePath", filePath);
        params.put("fileName", fileName);

        try (FileInputStream fis = new FileInputStream(exportTempPath)) {

            log.info("Start generating reports");
            //构建Excel


            //读取本地的导出模板
            HSSFWorkbook workBook = new HSSFWorkbook(fis);
            for (int m = 0; m < workBook.getNumberOfSheets(); m++) {
                //获取对应的sheet
                HSSFSheet sheet = workBook.getSheetAt(m);
                String sheetName = sheet.getSheetName();
                if (!sheetName.equalsIgnoreCase("Z08 一般公共预算财政拨款支出决算明细表(财决08表)")
                        && !sheetName.equalsIgnoreCase("Z05_3 经营支出决算明细表(财决05-3表)")
                        && !sheetName.equalsIgnoreCase("Z05_1 基本支出决算明细表(财决05-1表)")
                        && !sheetName.equalsIgnoreCase("Z05 支出决算明细表(财决05表)")
                        && !sheetName.equalsIgnoreCase("Z04 支出决算表(财决04表)")
                        && !sheetName.equalsIgnoreCase("Z03 收入决算表(财决03表)")
                        && !sheetName.equalsIgnoreCase("Z08_1 一般公共预算财政拨款基本支出决算明细表(财决08-")
                ) {
                    continue;
                }

                /*遍历表中的行的*/
                for (int i = 9; i < sheet.getLastRowNum(); i++) {
                    if (sheet.getRow(i) == null) {
                        //空行
                        continue;
                    }
                    if (StringUtils.isEmpty(ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(4)))) {
                        continue; //如果合计列为空 则认为是空行
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
                    List<ReportRsp> dataList = reportService.getData(paramsMap);

                    int total = 0;//合计的列
                    /*遍历表中的列*/
                    for (int j = 4; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                        BigDecimal bigDecimal = new BigDecimal("0.00"); //统计符合列的数据

                        HSSFCell cell = sheet.getRow(i).getCell(j);
                        String cellVal = ExcelUtil.getStringValueFromCell(cell);//获取列的内容
                        /*如果是 一 则忽略*/
                        if (cellVal.equalsIgnoreCase("\\一")) {
                            continue;
                        }
                        if (cellVal.equalsIgnoreCase("sum")) {//代表合计
                            total = j;//记录合计的列号
                            continue;//不往下执行
                        }

                        if (cellVal.contains("(") || cellVal.contains("（")) {//考虑到中英文
                            // ,    ，
                            String splitFirst = "";//是否是sum
                            String str = "";//获取()内的数据
                            if (cellVal.split("\\(").length > 1) {
                                splitFirst = cellVal.split("\\(")[0];
                                //说明是 ( 这个符号(英文)
                                //获取里面的值
                                int start = cellVal.indexOf("(") + 1;//截取开始值
                                int end = cellVal.indexOf(")");//截取结束值
                                str = cellVal.substring(start, end);//获取()内的数据
                            } else if (cellVal.split("（").length > 1) {
                                splitFirst = cellVal.split("（")[0];
                                //说明是 （ 这个符号(中文)
                                //说明是 ( 这个符号(英文)
                                //获取里面的值
                                int start = cellVal.indexOf("（") + 1;//截取开始值
                                int end = cellVal.indexOf("）");//截取结束值
                                str = cellVal.substring(start, end);//获取()内的数据
                            } else {
                                continue;
                            }
                            int size = 0;//小计长度
                            if (str.split(",").length > 1) {
                                size = StringUtils.getObjInt(str.split(",")[1]) - StringUtils.getObjInt(str.split(",")[0]) + 1;
                            } else if (str.split("，").length > 1) {
                                size = StringUtils.getObjInt(str.split("，")[1]) - StringUtils.getObjInt(str.split("，")[0]) + 1;
                            } else {
                                continue;
                            }

                            if (splitFirst.equalsIgnoreCase("sum")) {//代表小计
                                size += j;//小计的长度
                                BigDecimal subtotal = new BigDecimal("0.00");//小计金额
                                for (int k = j + 1; k < size + 1; k++) {
                                    HSSFCell cell2 = sheet.getRow(i).getCell(k);
                                    String cellVal2 = ExcelUtil.getStringValueFromCell(cell2);//获取列的内容
                                    /*如果是 一 则忽略*/
                                    if (cellVal2.equalsIgnoreCase("\\一")) {
                                        continue;
                                    }
                                    BigDecimal bigDecimal2 = new BigDecimal("0.00"); //统计符合列的数据
                                    /*值为空则设置为0*/
                                    if (StringUtils.isEmpty(cellVal2)) {
                                        cell2.setCellValue(bigDecimal2.toString());
                                    } else {
                                        if (dataList != null && dataList.size() > 0) {
                                            String[] split2 = cellVal2.split("\\+");
                                            for (int l = 0; l < split2.length; l++) {
                                                for (ReportRsp reportRsp : dataList) {
                                                    String accCode = reportRsp.getAccCode();
                                                    if (accCode.length() >= split2[l].length()) {
                                                        if (split2[l].equalsIgnoreCase(accCode.substring(0, split2[l].length()))) {
                                                            bigDecimal2 = bigDecimal2.add(new BigDecimal(reportRsp.getCaAmt()));
                                                        }
                                                    }
                                                }
                                            }
                                            cell2.setCellValue(bigDecimal2.toString());
                                        } else {
                                            cell2.setCellValue(bigDecimal2.toString());
                                        }
                                        subtotal = subtotal.add(bigDecimal2);
                                    }
                                }
                                sum = sum.add(subtotal);//将数量加到合计中
                                cell.setCellValue(subtotal.toString());//将小计的值赋值
                                j = size;
                                continue; //不往下执行
                            }
                            {
                                continue;
                            }
                        }

                        /*值为空则设置为0*/
                        if (StringUtils.isEmpty(cellVal)) {
                            cell.setCellValue(bigDecimal.toString());
                        } else {
                            if (dataList != null && dataList.size() > 0) {
                                String[] split = cellVal.split("\\+");
                                for (int k = 0; k < split.length; k++) {
                                    for (ReportRsp reportRsp : dataList) {
                                        String accCode = reportRsp.getAccCode();
                                        if (accCode.length() >= split[k].length()) {
                                            if (split[k].equalsIgnoreCase(accCode.substring(0, split[k].length()))) {
                                                bigDecimal = bigDecimal.add(new BigDecimal(reportRsp.getCaAmt()));
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
                    sheet.getRow(i).getCell(total).setCellValue(sum.toString());//给合计赋值
                }
            }
            FileUtils.judeDirExists(rootPath + filePath);
            FileUtils.judeFileExists(excelPath);
            try (FileOutputStream out = new FileOutputStream(excelPath)) {
                workBook.write(out); //写回数据
            }

            params.put("status", "1");
            params.put("msg", "处理成功");
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("导出数据失败!", e);
            params.put("status", "2");
            params.put("msg", e.toString());
            return AjaxResult.error();
        } finally {
            //保存数据
            int i = asynDownService.saveFile(params);
        }

    }

}
