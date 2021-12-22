package com.baobiao.project.report.report.controller;

import com.baobiao.common.constant.Constans;
import com.baobiao.common.utils.DateUtils;
import com.baobiao.common.utils.PoiUtil;
import com.baobiao.common.utils.StringUtils;
import com.baobiao.common.utils.UploadUtils;
import com.baobiao.common.utils.bean.Map2Bean;
import com.baobiao.common.utils.file.FileUtils;
import com.baobiao.common.utils.poi.ExcelUtil;
import com.baobiao.common.utils.spring.SpringUtils;
import com.baobiao.common.utils.uuid.IdUtils;
import com.baobiao.framework.aspectj.lang.annotation.Log;
import com.baobiao.framework.aspectj.lang.enums.BusinessType;
import com.baobiao.framework.config.RuoYiConfig;
import com.baobiao.framework.web.controller.BaseController;
import com.baobiao.framework.web.domain.AjaxResult;
import com.baobiao.framework.web.page.TableDataInfo;
import com.baobiao.project.report.dto.ReportCondition;
import com.baobiao.project.report.report.domain.AsynDown;
import com.baobiao.project.report.report.domain.ReportRsp;
import com.baobiao.project.report.report.service.AsynDownService;
import com.baobiao.project.report.report.service.ReportService;
import com.baobiao.project.system.dept.domain.Dept;
import com.baobiao.project.system.dept.service.IDeptService;
import com.baobiao.project.system.dict.domain.DictData;
import com.baobiao.project.system.dict.service.IDictDataService;
import com.baobiao.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private IDeptService iDeptService;

    @Autowired
    private AsynDownService asynDownService;

    @Autowired
    private IDictDataService dictDataService;


    private String prefix = "report";

    @RequiresPermissions("report:report:view")
    @GetMapping()
    public String report() {
        return prefix + "/report";
    }

    @RequiresPermissions("report:report:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AsynDown asynDown) {
        log.info("开始查询导出报表数据");
        if (StringUtils.isEmpty(asynDown.getDeptId())) {
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            /*由于刚开始设计有问题  此处deptName 就是 deptId*/
            asynDown.setParentId(user.getDeptId());
        }
        startPage();
        List<AsynDown> list = asynDownService.selectAsynDownList(asynDown);
        return getDataTable(list);
    }

    @Log(title = "报表统计")
    @GetMapping("/scheduled")
    @ResponseBody
    public AjaxResult scheduled() {
        //查询师傅还有在处理中的任务 如果有则定时刷新任务继续
        int result = asynDownService.countByStatus();
        if (result > 0) {
            return AjaxResult.error(); //继续执行定时任务
        } else {
            return AjaxResult.success();
        }
    }


    @Log(title = "报表统计")
    @RequiresPermissions("report:report:task")
    @PostMapping("/task")
    @ResponseBody
    public AjaxResult createTask(ReportCondition reportCondition) {
        /*获取全部机构*/
        List<Dept> deptList = iDeptService.selectDeptIdList();
        if (deptList != null && deptList.size() > 0) {
            asynDownService.deleteAll();
            /*重新创建任务*/
            for (Dept dept : deptList) {
                AsynDown asynDown = new AsynDown();
                asynDown.setId(IdUtils.simpleUUID());
                asynDown.setFileName("2020_" + dept.getDeptName() + DateUtils.dateTime() + ".xls");
                asynDown.setFilePath("1");
                asynDown.setStatus(Constans.AsynDownStatus.PROCESSED_INIT.getValue());//处理中
                asynDown.setMsg("任务初始化...");
                asynDown.setDeptId(dept.getDeptId());
                // asynDown.setTimeInterval("2020-01至2020-12");
                asynDown.setTimeInterval(reportCondition.getBeginTime() + "至" + reportCondition.getEndTime());
                asynDownService.saveFile(asynDown);
            }
        }
        return AjaxResult.success();
    }


    /**
     * 报表模板导入
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Log(title = "报表统计", businessType = BusinessType.IMPORT)
    @RequiresPermissions("report:report:import")
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
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(asynDown.getFileName(), "UTF-8"));
            FileUtils.setAttachmentResponseHeader(response, asynDown.getFileName());
            FileUtils.writeBytes(filePath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("report:report:remove")
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
    // @RequiresPermissions("report:report:export")
    @RequestMapping("/export")
    @ResponseBody
    public AjaxResult exportReport(ReportCondition reportCondition) {
        // reportCondition.setBeginTime("2020-01");
        // reportCondition.setEndTime("2020-12");
        String rootPath = RuoYiConfig.getProfile();//根路径
        String filePath = RuoYiConfig.getAsynDown();//文件路径
        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringUtils.getBean("threadPoolTaskExecutor");
        if (!StringUtils.isEmpty(reportCondition.getId())) {
            //更新单个
            AsynDown asynDown = asynDownService.selectAsynDown(reportCondition.getId());
            if (asynDown == null) {
                return AjaxResult.error("文件ID不存在");
            }
            /*修改文件名后拼接的日期*/
            String fileName = asynDown.getFileName();
            if (!StringUtils.isEmpty(fileName)) {
                int i = fileName.indexOf(".");
                String fileNamePrefix = fileName.substring(0, i - 8);
                asynDown.setFileName(fileNamePrefix + DateUtils.dateTime() + ".xls");
            }
            String excelPath = rootPath + filePath + asynDown.getFileName();
            asynDown.setFilePath(filePath);
            asynDown.setStatus(Constans.AsynDownStatus.PROCESSING.getValue());//处理中
            asynDown.setMsg("处理中...");
            asynDown.setTimeInterval(reportCondition.getBeginTime() + "至" + reportCondition.getEndTime());
            asynDownService.update(asynDown);
            /*启动一个单独的线程  处理任务*/
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    executeTask2(asynDown, reportCondition, rootPath + filePath, excelPath);
                }
            });
        } else {
            //更新所有
            List<AsynDown> asynDowns = asynDownService.selectAsynDownList(new AsynDown());
            if (asynDowns == null && asynDowns.size() < 1) {
                return AjaxResult.error("文件不存在");
            }
            for (AsynDown asynDown : asynDowns) {
                /*修改文件名后拼接的日期*/
                String fileName = asynDown.getFileName();
                if (!StringUtils.isEmpty(fileName)) {
                    int i = fileName.indexOf(".");
                    String fileNamePrefix = fileName.substring(0, i - 8);
                    asynDown.setFileName(fileNamePrefix + DateUtils.dateTime() + ".xls");
                }
                String excelPath = rootPath + filePath + asynDown.getFileName();
                asynDown.setFilePath(filePath);
                asynDown.setStatus(Constans.AsynDownStatus.PROCESSING.getValue());//处理中
                asynDown.setMsg("处理中...");
                asynDown.setTimeInterval(reportCondition.getBeginTime() + "至" + reportCondition.getEndTime());
                asynDownService.update(asynDown);
                /*启动一个单独的线程  处理任务*/
                threadPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        executeTask2(asynDown, reportCondition, rootPath + filePath, excelPath);
                    }
                });
            }

        }
        return AjaxResult.success();
    }

    /**
     * 导出excel任务
     *
     * @param asynDown        文件信息
     * @param reportCondition 条件信息
     * @param folderPath      文件夹路径
     * @param excelPath       文件路径
     */
    private void executeTask(AsynDown asynDown, ReportCondition reportCondition, String folderPath, String excelPath) {
        //导出模板的路径
        String tempFileName = "ReportTemp.xls";
        String exportTempPath = RuoYiConfig.getReportPath() + tempFileName;

        Map sessionMap = new HashMap();

        try (FileInputStream fis = new FileInputStream(exportTempPath)) {

            log.info("Start generating reports");
            //构建Excel
            String deptId = asynDown.getDeptId();
            reportCondition.setDeptId(deptId);
            //读取本地的导出模板
            HSSFWorkbook workBook = new HSSFWorkbook(fis);
            /*设置单元格格式*/
            CellStyle style = workBook.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));

            style.setAlignment(HorizontalAlignment.RIGHT);// 靠右
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            /*边框*/
            style.setBorderBottom(BorderStyle.THIN); //下边框
            style.setBorderLeft(BorderStyle.THIN);//左边框
            style.setBorderTop(BorderStyle.THIN);//上边框
            style.setBorderRight(BorderStyle.THIN);//右边框
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());



            /*bean转成map*/
            Map<String, Object> beanMap = Map2Bean.transBean2Map(reportCondition);

            for (int m = 0; m < workBook.getNumberOfSheets(); m++) {
                //获取对应的sheet
                HSSFSheet sheet = workBook.getSheetAt(m);
                String sheetName = sheet.getSheetName();
                if (!sheetName.equalsIgnoreCase("Z08 一般公共预算财政拨款支出决算明细表(财决08表)")
                        && !sheetName.equalsIgnoreCase("Z03 收入决算表(财决03表)")
                        && !sheetName.equalsIgnoreCase("Z04 支出决算表(财决04表)")
                        && !sheetName.equalsIgnoreCase("Z05 支出决算明细表(财决05表)")
                        && !sheetName.equalsIgnoreCase("Z05_1 基本支出决算明细表(财决05-1表)")
                        && !sheetName.equalsIgnoreCase("Z05_2 项目支出决算明细表(财决05-2表)")
                        && !sheetName.equalsIgnoreCase("Z05_3 经营支出决算明细表(财决05-3表)")
                        && !sheetName.equalsIgnoreCase("Z07 一般公共预算财政拨款收入支出决算表(财决07表)")
                        && !sheetName.equalsIgnoreCase("Z08_1 一般公共预算财政拨款基本支出决算明细表(财决08-")
                        && !sheetName.equalsIgnoreCase("Z08_2 一般公共预算财政拨款项目支出决算明细表(财决08-")
                        && !sheetName.equalsIgnoreCase("F01 预算支出相关信息表(财决附01表)")
                        && !sheetName.equalsIgnoreCase("F03 机构运行信息表(财决附03表)")
                        && !sheetName.equalsIgnoreCase("CS03 其他收入等明细情况表")
                        && !sheetName.equalsIgnoreCase("QB12 资产负债简表")
                        && !sheetName.equalsIgnoreCase("QB01 非中央财政拨款收入明细表")
                        && !sheetName.equalsIgnoreCase("QB03 非中央财政拨款支出决算明细表")
                        && !sheetName.equalsIgnoreCase("QB04中央财政拨款人员经费明细表")
                        && !sheetName.equalsIgnoreCase("QB05人员公用支出明细表")
                ) {
                    continue;
                }
                log.info("开始处理{}表", sheetName);

                DictData dictData = new DictData();
                dictData.setDictLabel(sheetName);
                dictData = dictDataService.selectByDictLaber(dictData);
                String dictValue = dictData.getDictValue();
                int beginRow = StringUtils.getObjInt(dictValue.split("/")[0]);
                int beginClo = StringUtils.getObjInt(dictValue.split("/")[1]);//开始写的第一列数据
                int delete = 0; //是否有删除的行

                if (Constans.reportCss.STYLE_ONE.getValue().equalsIgnoreCase(dictData.getCssClass())) {//判断模板类型
                    //添加时间
                    beanMap.put("year", StringUtils.getObjStr(beanMap.get("beginTime")).substring(0, 4));
                    List<Map<String, Object>> bAccCodeList = reportService.getBAccCode(beanMap);

                    if (bAccCodeList != null && bAccCodeList.size() > 0) {
                        /*遍历表中的行的*/
                        for (int i = 0; i < bAccCodeList.size(); i++) {
                            /*获取baccCode*/
                            // String bAccCode = ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(0));
                            String bAccCode = StringUtils.getObjStr(bAccCodeList.get(i).get("bAccCode"));
                            String bAccName = StringUtils.getObjStr(bAccCodeList.get(i).get("bAccName"));

                            if (StringUtils.isEmpty(bAccCode)) {
                                continue;
                            }
                            //获取行数
                            int lastRowNum = sheet.getLastRowNum();
                            int nextRowNum = i + beginRow + delete;
                            if (lastRowNum - nextRowNum <= 3) {
                                //添加行
                                PoiUtil.insertRow(sheet, nextRowNum, 1, beginRow);
                                sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
                            }

                            HSSFCell cellBAccCode = sheet.getRow(i + beginRow + delete).getCell(0);
                            cellBAccCode.setCellStyle(style);
                            cellBAccCode.setCellValue(bAccCode);
                            sheet.getRow(i + beginRow + delete).getCell(3).setCellValue(bAccName);

                            Map<String, Object> paramsMap = new HashMap();
                            // paramsMap = Map2Bean.transBean2Map(reportCondition);
                            paramsMap.putAll(beanMap);//添加查询条件
                            paramsMap.put("bAccCode", bAccCode);
                            // paramsMap.put("deptName", deptId);
                            List<ReportRsp> dataList = reportService.getData(paramsMap);

                            /*遍历表中的列*/
                            Map resultMap = getCellVal(sheet, i + beginRow + delete, beginClo, deptId, bAccCode, "", sessionMap, style, true, dataList, reportCondition);

                            int total = StringUtils.getObjInt(resultMap.get("total"));
                            BigDecimal sum = new BigDecimal(StringUtils.getObjStr(resultMap.get("sum")));

                            // if (total != 0) { //如果total为零  则认为没有这一列

                            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                if (i == bAccCodeList.size() - 1) { //针对最后一行
                                    ExcelUtil.removeRow(sheet, i + beginRow + delete);
                                }
                                delete--;
                            } else {
                                if (total != 0) { //如果total为零  则认为没有这一列
                                    HSSFCell cellSum = sheet.getRow(i + beginRow + delete).getCell(total);
                                    cellSum.setCellStyle(style);
                                    cellSum.setCellValue(sum.doubleValue());

                                    /*  统计第一行的合计值*/
                                    if (sessionMap.containsKey("sum" + total)) {
                                        sessionMap.put("sum" + total, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + total))).add(sum));
                                    } else {
                                        sessionMap.put("sum" + total, sum);
                                    }
                                }
                            }
                        }
                    } else {
                        //删除模板行数据
                        ExcelUtil.removeRow(sheet, beginRow);
                    }

                    /*  给第一行的合计值 赋值*/
                    for (int i = beginClo; i < sheet.getRow(beginRow - 1).getPhysicalNumberOfCells(); i++) {
                        HSSFCell cellTotal = sheet.getRow(beginRow - 1).getCell(i);
                        cellTotal.setCellStyle(style);
                        cellTotal.setCellValue(new BigDecimal(StringUtils.getObjStrBigDeci(sessionMap.get("sum" + i))).doubleValue());//给合计赋值
                    }
                    sessionMap.clear();
                } else if (Constans.reportCss.STYLE_TWO.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                        // 删除空行
                        Row row = sheet.getRow(i);
                        if (null == row) {
                            continue; //针对空行
                        }
                        for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                            HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                            if (cellVal.contains("*")) {
                                cellVal = cellVal.substring(1, cellVal.length() - 1);//获取值
                                Map<String, Object> paramsMap = new HashMap();
                                // paramsMap = Map2Bean.transBean2Map(reportCondition);
                                paramsMap.putAll(beanMap);//添加筛选条件
                                paramsMap.put("acc_code", cellVal);
                                // paramsMap.put("deptName", deptId);
                                BigDecimal FTempResult = reportService.getDataByAccCode(paramsMap);
                                cellsetVal.setCellValue(FTempResult.doubleValue());
                                cellsetVal.setCellStyle(style);
                            }
                            continue;
                        }
                    }
                } else if (Constans.reportCss.STYLE_THREE.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                        // 删除空行
                        Row row = sheet.getRow(i);
                        if (null == row) {
                            continue; //针对空行
                        }
                        for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                            HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                            if (StringUtils.isEmpty(cellVal)) {
                                continue;
                            }
                            /*替换可能存在的中文字符*/
                            cellVal.replace("（", "(");
                            cellVal.replace("）", ")");
                            cellVal.replace("，", ",");
                            cellVal.replace("：", ":");
                            cellVal.replace("，", ",");

                            if (cellVal.contains("(")) {
                                String splitFirst = "";//是否是sum
                                String str = "";//获取()内的数据
                                if (cellVal.split("\\(").length > 1) {
                                    splitFirst = cellVal.split("\\(")[0];
                                    //说明是 ( 这个符号(英文)
                                    //获取里面的值
                                    int start = cellVal.indexOf("(") + 1;//截取开始值
                                    int end = cellVal.indexOf(")");//截取结束值
                                    str = cellVal.substring(start, end);//获取()内的数据
                                } else {
                                    log.warn("{}格式不正确", cellVal);
                                    continue;
                                }
                                int size = 0;//小计长度
                                if (str.split(",").length > 1) {
                                    size = StringUtils.getObjInt(str.split(",")[1]) - StringUtils.getObjInt(str.split(",")[0]) + 1;
                                } else {
                                    log.warn("{}格式不正确", cellVal);
                                    continue;
                                }
                                size += j;//小计的长度
                                BigDecimal subtotal = new BigDecimal("0.00");//小计金额

                                /**
                                 * 小计这段的数据的操作
                                 */
                                for (int k = j + 1; k < size + 1; k++) {
                                    HSSFCell cell2setValue = sheet.getRow(i).getCell(k);
                                    String cellVal2 = ExcelUtil.getStringValueFromCell(cell2setValue);//获取列的内容
                                    if (cellVal2.contains("*")) {
                                        cellVal2 = cellVal2.substring(1, cellVal2.length() - 1);//获取值
                                        Map paramsMap = new HashMap();
                                        paramsMap.putAll(beanMap);//添加筛选条件
                                        paramsMap.put("acc_code", cellVal2);
                                        // paramsMap.put("deptName", deptId);
                                        BigDecimal FTempResult = reportService.getDataByAccCode(paramsMap);
                                        cell2setValue.setCellValue(FTempResult.doubleValue());
                                        cell2setValue.setCellStyle(style);
                                        subtotal = subtotal.add(FTempResult);
                                    }
                                }
                                j = size;
                                cellsetVal.setCellValue(subtotal.doubleValue());
                                cellsetVal.setCellStyle(style);
                                continue;
                            }
                            if (cellVal.contains("*")) {
                                cellVal = cellVal.substring(1, cellVal.length() - 1);//获取值
                                Map paramsMap = new HashMap();
                                paramsMap.putAll(beanMap);//添加筛选条件
                                paramsMap.put("acc_code", cellVal);
                                // paramsMap.put("deptName", deptId);
                                BigDecimal FTempResult = reportService.getDataByAccCode(paramsMap);
                                cellsetVal.setCellValue(FTempResult.doubleValue());
                                cellsetVal.setCellStyle(style);
                                continue;
                            }
                            if (cellVal.contains("{")) {//代表是json
                                Map<String, Object> paramMap = StringUtils.getStringToMap(cellVal);
                                // paramMap.put("deptName", deptId);
                                // paramMap.putAll(paramsMap);
                                paramMap.putAll(beanMap);//添加筛选条件
                                BigDecimal result = reportService.getCRAmt(paramMap);
                                cellsetVal.setCellValue(result.doubleValue());
                                cellsetVal.setCellStyle(style);
                                continue;
                            }
                        }
                    }
                } else if (Constans.reportCss.STYLE_FOUR.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    LinkedList<Map<String, Object>> itemCodeList = reportService.getItemCode(beanMap);
                    if (itemCodeList != null && itemCodeList.size() > 0) {
                        Map paramMap = new HashMap();
                        String year = StringUtils.getObjStr(beanMap.get("beginTime")).substring(0, 4);
                        paramMap.put("year", year);
                        paramMap.put("deptId", deptId);
                        //根据item_code 获取对应的item_name
                        /*遍历表中的行的*/
                        for (int i = 0; i < itemCodeList.size(); i++) {
                            /*获取baccCode*/
                            // String bAccCode = ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(0));
                            String bAccCode = StringUtils.getObjStr(itemCodeList.get(i).get("bAccCode"));
                            String itemCode = StringUtils.getObjStr(itemCodeList.get(i).get("itemCode"));
                            String itemName = "*";
                            //根据item_code 获取对应的item_name
                            paramMap.put("itemCode", itemCode);
                            if (!"*".equalsIgnoreCase(itemCode)) {
                                itemName = reportService.selectItemNameByItemCode(paramMap);
                                if (StringUtils.isEmpty(itemName)) {
                                    paramMap.put("itemCode", itemCode.substring(0, itemCode.length() - 2));
                                    itemName = reportService.selectItemNameByItemCode(paramMap);
                                }
                            }

                            if (StringUtils.isEmpty(itemCode)) {
                                continue;
                            }

                            //获取行数
                            int lastRowNum = sheet.getLastRowNum();
                            int nextRowNum = i + beginRow + delete;
                            if (lastRowNum - nextRowNum < 3) {
                                //添加行
                                PoiUtil.insertRow(sheet, nextRowNum, 1, beginRow);
                                sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
                            }
                            HSSFCell cellBAccCode = sheet.getRow(i + beginRow + delete).getCell(0);
                            cellBAccCode.setCellStyle(style);
                            cellBAccCode.setCellValue(bAccCode);
                            sheet.getRow(i + beginRow + delete).getCell(3).setCellValue(itemName);
                            sheet.getRow(i + beginRow + delete).getCell(4).setCellValue(itemCode);

                            Map<String, Object> paramsMap = new HashMap();
                            // paramsMap = Map2Bean.transBean2Map(reportCondition);
                            paramsMap.putAll(beanMap);//添加查询条件
                            paramsMap.put("bAccCode", bAccCode);
                            paramsMap.put("itemCode", itemCode);
                            // List<ReportRsp> dataList = reportService.getItemCodeData(paramsMap);
                            List<ReportRsp> dataList = reportService.getItemCodeData(paramsMap);


                            /*遍历表中的列*/
                            Map resultMap = getCellVal(sheet, i + beginRow + delete, beginClo, deptId, bAccCode, itemCode, sessionMap, style, true, dataList, reportCondition);

                            int total = StringUtils.getObjInt(resultMap.get("total"));
                            BigDecimal sum = new BigDecimal(StringUtils.getObjStr(resultMap.get("sum")));


                            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                if (i == itemCodeList.size() - 1) { //针对最后一行
                                    ExcelUtil.removeRow(sheet, i + beginRow + delete);
                                }
                                delete--;
                            } else {
                                if (total != 0) { //如果total为零  则认为没有这一列
                                    HSSFCell cellSum = sheet.getRow(i + beginRow + delete).getCell(total);
                                    cellSum.setCellStyle(style);
                                    cellSum.setCellValue(sum.doubleValue());

                                    /*  统计第一行的合计值*/
                                    if (sessionMap.containsKey("sum" + total)) {
                                        sessionMap.put("sum" + total, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + total))).add(sum));
                                    } else {
                                        sessionMap.put("sum" + total, sum);
                                    }
                                }
                            }
                        }
                    } else {
                        ExcelUtil.removeRow(sheet, beginRow);
                    }
                    /*  给第一行的合计值 赋值*/
                    for (int i = beginClo; i < sheet.getRow(beginRow - 1).getPhysicalNumberOfCells(); i++) {
                        HSSFCell cellTotal = sheet.getRow(beginRow - 1).getCell(i);
                        cellTotal.setCellStyle(style);
                        cellTotal.setCellValue(new BigDecimal(StringUtils.getObjStrBigDeci(sessionMap.get("sum" + i))).doubleValue());//给合计赋值
                    }
                    sessionMap.clear();
                } else if (Constans.reportCss.STYLE_FIVE.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                        // 删除空行
                        Row row = sheet.getRow(i);
                        if (null == row) {
                            continue; //针对空行
                        }
                        for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                            //存储accCode,  用于区分年初 年末
                            List<String> codeList = new ArrayList<>();
                            HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                            if (cellVal.contains("*")) {
                                cellVal = cellVal.substring(1, cellVal.length() - 1);//获取值
                                Map<String, Object> paramsMap = new HashMap();
                                // paramsMap = Map2Bean.transBean2Map(reportCondition);
                                //paramsMap.putAll(beanMap);//添加筛选条件
                                paramsMap.put("acc_code", cellVal);
                                if (codeList.contains(cellVal)) {
                                    //已存在  判定为年末数据
                                    // paramsMap.put("deptName", deptId);
                                    BigDecimal FTempResult = reportService.getEndDataByAccCode(paramsMap);
                                    cellsetVal.setCellValue(FTempResult.doubleValue());
                                    cellsetVal.setCellStyle(style);
                                }else{
                                    codeList.add(cellVal);
                                    //不存在判定为年初数据
                                    BigDecimal FTempResult = reportService.getBeginDataByAccCode(paramsMap);
                                    cellsetVal.setCellValue(FTempResult.doubleValue());
                                    cellsetVal.setCellStyle(style);
                                }


                            }
                            continue;
                        }
                    }
                }
            }

            FileUtils.judeDirExists(folderPath);
            FileUtils.judeFileExists(excelPath);
            try (FileOutputStream out = new FileOutputStream(excelPath)) {
                workBook.write(out); //写回数据
            }

            asynDown.setStatus(Constans.AsynDownStatus.PROCESSED_SUCC.getValue());
            asynDown.setMsg("处理成功");

        } catch (Exception e) {
            log.error("导出数据失败!", e);
            asynDown.setStatus(Constans.AsynDownStatus.PROCESSED_FAIL.getValue());
            asynDown.setMsg(e.toString());
        } finally {
            asynDownService.updateFile(asynDown);
        }

    }


    /**
     * 导出excel任务
     *
     * @param asynDown        文件信息
     * @param reportCondition 条件信息
     * @param folderPath      文件夹路径
     * @param excelPath       文件路径
     */
    private void executeTask2(AsynDown asynDown, ReportCondition reportCondition, String folderPath, String excelPath) {
        //导出模板的路径
        String tempFileName = "ReportTemp.xls";
        String exportTempPath = RuoYiConfig.getReportPath() + tempFileName;

        Map sessionMap = new HashMap();

        try (FileInputStream fis = new FileInputStream(exportTempPath)) {

            log.info("Start generating reports");
            //构建Excel
            String deptId = asynDown.getDeptId();
            reportCondition.setDeptId(deptId);
            //读取本地的导出模板
            HSSFWorkbook workBook = new HSSFWorkbook(fis);
            /*设置单元格格式*/
            CellStyle style = workBook.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));

            style.setAlignment(HorizontalAlignment.RIGHT);// 靠右
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            /*边框*/
            style.setBorderBottom(BorderStyle.THIN); //下边框
            style.setBorderLeft(BorderStyle.THIN);//左边框
            style.setBorderTop(BorderStyle.THIN);//上边框
            style.setBorderRight(BorderStyle.THIN);//右边框
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());



            /*bean转成map*/
            Map<String, Object> beanMap = Map2Bean.transBean2Map(reportCondition);

            for (int m = 0; m < workBook.getNumberOfSheets(); m++) {
                //获取对应的sheet
                HSSFSheet sheet = workBook.getSheetAt(m);
                String sheetName = sheet.getSheetName();
                if (!sheetName.equalsIgnoreCase("Z08 一般公共预算财政拨款支出决算明细表(财决08表)")
                        && !sheetName.equalsIgnoreCase("Z03 收入决算表(财决03表)")
                        && !sheetName.equalsIgnoreCase("Z04 支出决算表(财决04表)")
                        && !sheetName.equalsIgnoreCase("Z05 支出决算明细表(财决05表)")
                        && !sheetName.equalsIgnoreCase("Z05_1 基本支出决算明细表(财决05-1表)")
                        && !sheetName.equalsIgnoreCase("Z05_2 项目支出决算明细表(财决05-2表)")
                        && !sheetName.equalsIgnoreCase("Z05_3 经营支出决算明细表(财决05-3表)")
                        && !sheetName.equalsIgnoreCase("Z07 一般公共预算财政拨款收入支出决算表(财决07表)")
                        && !sheetName.equalsIgnoreCase("Z08_1 一般公共预算财政拨款基本支出决算明细表(财决08-")
                        && !sheetName.equalsIgnoreCase("Z08_2 一般公共预算财政拨款项目支出决算明细表(财决08-")
                        && !sheetName.equalsIgnoreCase("F01 预算支出相关信息表(财决附01表)")
                        && !sheetName.equalsIgnoreCase("F03 机构运行信息表(财决附03表)")
                        && !sheetName.equalsIgnoreCase("CS03 其他收入等明细情况表")
                        && !sheetName.equalsIgnoreCase("QB12 资产负债简表")
                        && !sheetName.equalsIgnoreCase("QB01 非中央财政拨款收入明细表")
                        && !sheetName.equalsIgnoreCase("QB03 非中央财政拨款支出决算明细表")
                        && !sheetName.equalsIgnoreCase("QB04中央财政拨款人员经费明细表")
                        && !sheetName.equalsIgnoreCase("QB05人员公用支出明细表")
                ) {
                    continue;
                }
                log.info("开始处理{}表", sheetName);

                DictData dictData = new DictData();
                dictData.setDictLabel(sheetName);
                dictData = dictDataService.selectByDictLaber(dictData);
                String dictValue = dictData.getDictValue();
                int beginRow = StringUtils.getObjInt(dictValue.split("/")[0]);
                int beginClo = StringUtils.getObjInt(dictValue.split("/")[1]);//开始写的第一列数据
                int delete = 0; //是否有删除的行

                if (Constans.reportCss.STYLE_ONE.getValue().equalsIgnoreCase(dictData.getCssClass())) {//判断模板类型
                    //添加时间
                    beanMap.put("year", StringUtils.getObjStr(beanMap.get("beginTime")).substring(0, 4));
                    List<Map<String, Object>> bAccCodeList = reportService.getBAccCode(beanMap);

                    if (bAccCodeList != null && bAccCodeList.size() > 0) {
                        /*遍历表中的行的*/
                        for (int i = 0; i < bAccCodeList.size(); i++) {
                            /*获取baccCode*/
                            // String bAccCode = ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(0));
                            String bAccCode = StringUtils.getObjStr(bAccCodeList.get(i).get("bAccCode"));
                            String bAccName = StringUtils.getObjStr(bAccCodeList.get(i).get("bAccName"));

                            if (StringUtils.isEmpty(bAccCode)) {
                                continue;
                            }
                            //获取行数
                            int lastRowNum = sheet.getLastRowNum();
                            int nextRowNum = i + beginRow + delete;
                            if (lastRowNum - nextRowNum <= 3) {
                                //添加行
                                PoiUtil.insertRow(sheet, nextRowNum, 1, beginRow);
                                sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
                            }

                            HSSFCell cellBAccCode = sheet.getRow(i + beginRow + delete).getCell(0);
                            cellBAccCode.setCellStyle(style);
                            cellBAccCode.setCellValue(bAccCode);
                            sheet.getRow(i + beginRow + delete).getCell(3).setCellValue(bAccName);

                            Map<String, Object> paramsMap = new HashMap();
                            // paramsMap = Map2Bean.transBean2Map(reportCondition);
                            paramsMap.putAll(beanMap);//添加查询条件
                            paramsMap.put("bAccCode", bAccCode);
                            // paramsMap.put("deptName", deptId);
                            List<ReportRsp> dataList = reportService.getData(paramsMap);

                            /*遍历表中的列*/
                            Map resultMap = getCellVal(sheet, i + beginRow + delete, beginClo, deptId, bAccCode, "", sessionMap, style, true, dataList, reportCondition);

                            int total = StringUtils.getObjInt(resultMap.get("total"));
                            BigDecimal sum = new BigDecimal(StringUtils.getObjStr(resultMap.get("sum")));

                            // if (total != 0) { //如果total为零  则认为没有这一列

                            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                if (i == bAccCodeList.size() - 1) { //针对最后一行
                                    ExcelUtil.removeRow(sheet, i + beginRow + delete);
                                }
                                delete--;
                            } else {
                                if (total != 0) { //如果total为零  则认为没有这一列
                                    HSSFCell cellSum = sheet.getRow(i + beginRow + delete).getCell(total);
                                    cellSum.setCellStyle(style);
                                    cellSum.setCellValue(sum.doubleValue());

                                    /*  统计第一行的合计值*/
                                    if (sessionMap.containsKey("sum" + total)) {
                                        sessionMap.put("sum" + total, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + total))).add(sum));
                                    } else {
                                        sessionMap.put("sum" + total, sum);
                                    }
                                }
                            }
                        }
                    } else {
                        //删除模板行数据
                        ExcelUtil.removeRow(sheet, beginRow);
                    }

                    /*  给第一行的合计值 赋值*/
                    for (int i = beginClo; i < sheet.getRow(beginRow - 1).getPhysicalNumberOfCells(); i++) {
                        HSSFCell cellTotal = sheet.getRow(beginRow - 1).getCell(i);
                        cellTotal.setCellStyle(style);
                        cellTotal.setCellValue(new BigDecimal(StringUtils.getObjStrBigDeci(sessionMap.get("sum" + i))).doubleValue());//给合计赋值
                    }
                    sessionMap.clear();
                } else if (Constans.reportCss.STYLE_TWO.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    //条件集合
                    List<String> accCodeList = new ArrayList<>();
                    //先读取一遍表  获取大致条件
                    for (int k = beginRow; k <= sheet.getLastRowNum(); k++) {
                        // 删除空行
                        Row row = sheet.getRow(k);
                        if (null == row) {
                            continue; //针对空行
                        }
                        for (int l = beginClo; l < sheet.getRow(k).getPhysicalNumberOfCells(); l++) {
                            HSSFCell cellsetVal = sheet.getRow(k).getCell(l);//
                            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                            if (cellVal.contains("*")) {
                                if (cellVal.trim().length() < 3) {
                                    //认为是空数据
                                    continue;
                                }
                                cellVal = cellVal.trim().substring(1, cellVal.length() - 1);//获取值
                                if (cellVal.contains("-")) {
                                    String[] split = cellVal.split("-");
                                    for (String s : split) {
                                        s = s.trim().substring(0, 1);//只获取第一位
                                        if (!accCodeList.contains(s)) {
                                            accCodeList.add(s);
                                        }
                                    }

                                } else if (cellVal.contains("+")) {
                                    String[] split = cellVal.split("\\+");
                                    for (String s : split) {
                                        s = s.trim().substring(0, 1);//只获取第一位
                                        if (!accCodeList.contains(s)) {
                                            accCodeList.add(s);
                                        }
                                    }
                                } else {
                                    cellVal = cellVal.trim().substring(0, 1);//只获取第一位
                                    if (!accCodeList.contains(cellVal)) {
                                        accCodeList.add(cellVal);
                                    }
                                }
                            }
                        }
                    }
                    /*根据获取条件去查询数据*/
                    if (accCodeList != null && accCodeList.size() > 0) {
                        List<ReportRsp> resultList = new ArrayList<>();//结果集
                        for (String accCode : accCodeList) {
                            beanMap.put("accCode", accCode);
                            List<ReportRsp> resList = reportService.getAccCode(beanMap);
                            resultList.addAll(resList);
                        }
                        for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                            // 删除空行
                            Row row = sheet.getRow(i);
                            if (null == row) {
                                continue; //针对空行
                            }
                            for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                                HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                                String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                                if (cellVal.contains("*")) {
                                    if (resultList != null && resultList.size() > 0) {
                                        BigDecimal FTempResult = new BigDecimal("0.00");
                                        if (cellVal.trim().length() < 3) {
                                            //认为是空数据
                                            cellsetVal.setCellValue(new BigDecimal("0.00").doubleValue());
                                            cellsetVal.setCellStyle(style);
                                            continue;
                                        }
                                        cellVal = cellVal.trim().substring(1, cellVal.length() - 1);//获取值

                                        int count = 1;
                                        if (cellVal.contains("-")) {
                                            String[] split = cellVal.split("-");
                                            for (String s : split) {
                                                if (count == 1) {
                                                    for (ReportRsp reportRsp : resultList) {
                                                        if (reportRsp.getAccCode().startsWith(s)) {
                                                            FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                        }
                                                    }
                                                    count++;
                                                } else {
                                                    for (ReportRsp reportRsp : resultList) {
                                                        if (reportRsp.getAccCode().startsWith(s)) {
                                                            FTempResult = FTempResult.subtract(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (cellVal.contains("+")) {
                                            String[] split = cellVal.split("\\+");
                                            for (String s : split) {
                                                for (ReportRsp reportRsp : resultList) {
                                                    if (reportRsp.getAccCode().startsWith(s)) {
                                                        FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                    }
                                                }
                                            }
                                        } else {
                                            for (ReportRsp reportRsp : resultList) {
                                                if (reportRsp.getAccCode().startsWith(cellVal)) {
                                                    FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                }
                                            }
                                        }
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                    } else {
                                        cellsetVal.setCellValue(new BigDecimal("0.00").doubleValue());
                                        cellsetVal.setCellStyle(style);
                                    }
                                }
                            }
                        }
                    } else {
                        //没有条件,则全部设置为0
                        for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                            // 删除空行
                            Row row = sheet.getRow(i);
                            if (null == row) {
                                continue; //针对空行
                            }
                            for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                                HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                                String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                                if (cellVal.contains("*")) {
                                    cellsetVal.setCellValue(new BigDecimal("0.00").doubleValue());
                                    cellsetVal.setCellStyle(style);
                                }
                            }
                        }
                    }
                } else if (Constans.reportCss.STYLE_THREE.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    //条件集合
                    List<String> accCodeList = new ArrayList<>();
                    for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                        // 删除空行
                        Row row = sheet.getRow(i);
                        if (null == row) {
                            continue; //针对空行
                        }
                        for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                            HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                            if (StringUtils.isEmpty(cellVal)) {
                                continue;
                            }
                            /*替换可能存在的中文字符*/
                            cellVal.replace("（", "(");
                            cellVal.replace("）", ")");
                            cellVal.replace("，", ",");
                            cellVal.replace("：", ":");
                            cellVal.replace("，", ",");

                            if (cellVal.contains("*")) {
                                if (cellVal.trim().length() < 3) {
                                    //认为是空数据
                                    continue;
                                }
                                cellVal = cellVal.substring(1, cellVal.length() - 1).trim();//获取值
                                if (cellVal.contains("-")) {
                                    String[] split = cellVal.split("-");
                                    for (String s : split) {
                                        s = s.trim().substring(0, 1);//只获取第一位
                                        if (!accCodeList.contains(s)) {
                                            accCodeList.add(s);
                                        }
                                    }

                                } else if (cellVal.contains("+")) {
                                    String[] split = cellVal.split("\\+");
                                    for (String s : split) {
                                        s = s.trim().substring(0, 1);//只获取第一位
                                        if (!accCodeList.contains(s)) {
                                            accCodeList.add(s);
                                        }
                                    }
                                } else {
                                    cellVal = cellVal.trim().substring(0, 1);//只获取第一位
                                    if (!accCodeList.contains(cellVal)) {
                                        accCodeList.add(cellVal);
                                    }
                                }
                                continue;
                            }

                            if (cellVal.contains("{")) {//代表是json
                                Map<String, Object> paramMap = null;
                                try {
                                    paramMap = StringUtils.getStringToMap(cellVal);
                                } catch (Exception e) {
                                    log.error("数据模板有误:{}", sheetName);
                                    asynDown.setStatus(Constans.AsynDownStatus.PROCESSED_FAIL.getValue());
                                    asynDown.setMsg(sheetName + "数据模板不合法");
                                }
                                if (paramMap != null && paramMap.size() > 0) {
                                    String acc_code = StringUtils.getObjStr(paramMap.get("acc_code"));
                                    if (acc_code.length() < 1) {
                                        continue;
                                    }
                                    if (!accCodeList.contains(acc_code.substring(0, 1))) {
                                        accCodeList.add(acc_code.substring(0, 1));
                                    }
                                }
                                continue;
                            }
                        }
                    }
                    List<ReportRsp> resultList = new ArrayList<>();
                    if (accCodeList != null && accCodeList.size() > 0) {
                        for (String s : accCodeList) {
                            beanMap.put("accCode", s);
                            List<ReportRsp> resList = reportService.getDataByCondition(beanMap);
                            resultList.addAll(resList);
                        }

                        for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                            // 删除空行
                            Row row = sheet.getRow(i);
                            if (null == row) {
                                continue; //针对空行
                            }
                            for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                                HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                                String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                                if (StringUtils.isEmpty(cellVal)) {
                                    continue;
                                }
                                /*替换可能存在的中文字符*/
                                cellVal.replace("（", "(");
                                cellVal.replace("）", ")");
                                cellVal.replace("，", ",");
                                cellVal.replace("：", ":");
                                cellVal.replace("，", ",");

                                if (resultList != null && resultList.size() > 0) {
                                    if (cellVal.contains("(")) {
                                        String splitFirst = "";//是否是sum
                                        String str = "";//获取()内的数据
                                        if (cellVal.split("\\(").length > 1) {
                                            splitFirst = cellVal.split("\\(")[0];
                                            //说明是 ( 这个符号(英文)
                                            //获取里面的值
                                            int start = cellVal.indexOf("(") + 1;//截取开始值
                                            int end = cellVal.indexOf(")");//截取结束值
                                            str = cellVal.substring(start, end);//获取()内的数据
                                        } else {
                                            log.warn("{}格式不正确", cellVal);
                                            continue;
                                        }
                                        int size = 0;//小计长度
                                        if (str.split(",").length > 1) {
                                            size = StringUtils.getObjInt(str.split(",")[1]) - StringUtils.getObjInt(str.split(",")[0]) + 1;
                                        } else {
                                            log.warn("{}格式不正确", cellVal);
                                            continue;
                                        }
                                        size += j;//小计的长度
                                        BigDecimal subtotal = new BigDecimal("0.00");//小计金额

                                        /**
                                         * 小计这段的数据的操作
                                         */
                                        for (int k = j + 1; k < size + 1; k++) {
                                            HSSFCell cell2setValue = sheet.getRow(i).getCell(k);
                                            String cellVal2 = ExcelUtil.getStringValueFromCell(cell2setValue);//获取列的内容
                                            if (cellVal2.contains("*")) {
                                                BigDecimal FTempResult = new BigDecimal("0.00");
                                                cellVal2 = cellVal2.substring(1, cellVal2.length() - 1).trim();//获取值
                                                if (cellVal2.length() < 3) {
                                                    cellsetVal.setCellValue(FTempResult.doubleValue());
                                                    cellsetVal.setCellStyle(style);
                                                    continue;
                                                }
                                                int count = 1;
                                                if (cellVal2.contains("-")) {
                                                    String[] split = cellVal2.split("-");
                                                    for (String s : split) {
                                                        if (count == 1) {
                                                            for (ReportRsp reportRsp : resultList) {
                                                                if (reportRsp.getAccCode().startsWith(s)) {
                                                                    FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                                }
                                                            }
                                                            count++;
                                                        } else {
                                                            for (ReportRsp reportRsp : resultList) {
                                                                if (reportRsp.getAccCode().startsWith(s)) {
                                                                    FTempResult = FTempResult.subtract(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if (cellVal2.contains("+")) {
                                                    String[] split = cellVal2.split("\\+");
                                                    for (String s : split) {
                                                        for (ReportRsp reportRsp : resultList) {
                                                            if (reportRsp.getAccCode().startsWith(s)) {
                                                                FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    for (ReportRsp reportRsp : resultList) {
                                                        if (reportRsp.getAccCode().startsWith(cellVal2)) {
                                                            FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                        }
                                                    }
                                                }

                                                cell2setValue.setCellValue(FTempResult.doubleValue());
                                                cell2setValue.setCellStyle(style);
                                                subtotal = subtotal.add(FTempResult);
                                            }
                                        }
                                        j = size;
                                        cellsetVal.setCellValue(subtotal.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                    if (cellVal.contains("*")) {
                                        BigDecimal FTempResult = new BigDecimal("0.00");
                                        int count = 1;
                                        if (cellVal.contains("-")) {
                                            String[] split = cellVal.split("-");
                                            for (String s : split) {
                                                if (count == 1) {
                                                    for (ReportRsp reportRsp : resultList) {
                                                        if (reportRsp.getAccCode().startsWith(s)) {
                                                            FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                        }
                                                    }
                                                    count++;
                                                } else {
                                                    for (ReportRsp reportRsp : resultList) {
                                                        if (reportRsp.getAccCode().startsWith(s)) {
                                                            FTempResult = FTempResult.subtract(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (cellVal.contains("+")) {
                                            String[] split = cellVal.split("\\+");
                                            for (String s : split) {
                                                for (ReportRsp reportRsp : resultList) {
                                                    if (reportRsp.getAccCode().startsWith(s)) {
                                                        FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                    }
                                                }
                                            }
                                        } else {
                                            for (ReportRsp reportRsp : resultList) {
                                                if (reportRsp.getAccCode().startsWith(cellVal)) {
                                                    FTempResult = FTempResult.add(new BigDecimal(StringUtils.getObjStrBigDeci(reportRsp.getAmt())));
                                                }
                                            }
                                        }
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                    if (cellVal.contains("{")) {//代表是json
                                        Map<String, Object> paramMap = StringUtils.getStringToMap(cellVal);
                                        BigDecimal result = new BigDecimal("0.00");
                                        for (ReportRsp reportRsp : resultList) {
                                            String acc_code = StringUtils.getObjStr(paramMap.get("acc_code"));
                                            //paramMap.remove("acc_code");//移除
                                            if (reportRsp.getAccCode().startsWith(acc_code)) {
                                                Map<String, Object> reportMap = Map2Bean.transBean2Map(reportRsp);
                                                int flag = 1;
                                                for (String key : paramMap.keySet()) {
                                                    if (!"acc_code".equalsIgnoreCase(key)) {
                                                        String value = StringUtils.getObjStr(reportMap.get(key));
                                                        String tempVal = StringUtils.getObjStr(paramMap.get(key));
                                                        tempVal = tempVal.substring(0, tempVal.length() - 1);//去掉最后的%号
                                                        //对比
                                                        if (!value.startsWith(tempVal)) {
                                                            flag = 0;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (flag != 0) {
                                                    result = result.add(new BigDecimal(reportRsp.getAmt()));
                                                }
                                            }
                                        }
                                        cellsetVal.setCellValue(result.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                } else {
                                    BigDecimal FTempResult = new BigDecimal("0.00");
                                    if (cellVal.contains("(")) {
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                    if (cellVal.contains("*")) {
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                    if (cellVal.contains("{")) {//代表是json
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                }
                            }
                        }
                    } else {
                        BigDecimal FTempResult = new BigDecimal("0.00");
                        //设置为0
                        for (int i = beginRow; i <= sheet.getLastRowNum(); i++) {
                            // 删除空行
                            Row row = sheet.getRow(i);
                            if (null == row) {
                                continue; //针对空行
                            }
                            for (int j = beginClo; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
                                HSSFCell cellsetVal = sheet.getRow(i).getCell(j);//
                                String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                                if (StringUtils.isEmpty(cellVal)) {
                                    continue;
                                }
                                /*替换可能存在的中文字符*/
                                cellVal.replace("（", "(");
                                cellVal.replace("）", ")");
                                cellVal.replace("，", ",");
                                cellVal.replace("：", ":");
                                cellVal.replace("，", ",");

                                if (resultList != null && resultList.size() > 0) {
                                    if (cellVal.contains("(")) {
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                    if (cellVal.contains("*")) {
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                    if (cellVal.contains("{")) {//代表是json
                                        cellsetVal.setCellValue(FTempResult.doubleValue());
                                        cellsetVal.setCellStyle(style);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                } else if (Constans.reportCss.STYLE_FOUR.getValue().equalsIgnoreCase(dictData.getCssClass())) {
                    LinkedList<Map<String, Object>> itemCodeList = reportService.getItemCode(beanMap);
                    if (itemCodeList != null && itemCodeList.size() > 0) {
                        Map paramMap = new HashMap();
                        String year = StringUtils.getObjStr(beanMap.get("beginTime")).substring(0, 4);
                        paramMap.put("year", year);
                        paramMap.put("deptId", deptId);
                        //根据item_code 获取对应的item_name
                        /*遍历表中的行的*/
                        for (int i = 0; i < itemCodeList.size(); i++) {
                            /*获取baccCode*/
                            // String bAccCode = ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(0));
                            String bAccCode = StringUtils.getObjStr(itemCodeList.get(i).get("bAccCode"));
                            String itemCode = StringUtils.getObjStr(itemCodeList.get(i).get("itemCode"));
                            String itemName = "*";
                            //根据item_code 获取对应的item_name
                            paramMap.put("itemCode", itemCode);
                            if (!"*".equalsIgnoreCase(itemCode)) {
                                itemName = reportService.selectItemNameByItemCode(paramMap);
                                if (StringUtils.isEmpty(itemName)) {
                                    paramMap.put("itemCode", itemCode.substring(0, itemCode.length() - 2));
                                    itemName = reportService.selectItemNameByItemCode(paramMap);
                                }
                            }

                            if (StringUtils.isEmpty(itemCode)) {
                                continue;
                            }

                            //获取行数
                            int lastRowNum = sheet.getLastRowNum();
                            int nextRowNum = i + beginRow + delete;
                            if (lastRowNum - nextRowNum < 3) {
                                //添加行
                                PoiUtil.insertRow(sheet, nextRowNum, 1, beginRow);
                                sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
                            }
                            HSSFCell cellBAccCode = sheet.getRow(i + beginRow + delete).getCell(0);
                            cellBAccCode.setCellStyle(style);
                            cellBAccCode.setCellValue(bAccCode);
                            sheet.getRow(i + beginRow + delete).getCell(4).setCellValue(itemName);
                            sheet.getRow(i + beginRow + delete).getCell(5).setCellValue(itemCode);

                            Map<String, Object> paramsMap = new HashMap();
                            // paramsMap = Map2Bean.transBean2Map(reportCondition);
                            paramsMap.putAll(beanMap);//添加查询条件
                            paramsMap.put("bAccCode", bAccCode);
                            paramsMap.put("itemCode", itemCode);
                            // List<ReportRsp> dataList = reportService.getItemCodeData(paramsMap);
                            List<ReportRsp> dataList = reportService.getItemCodeData(paramsMap);


                            /*遍历表中的列*/
                            Map resultMap = getCellVal(sheet, i + beginRow + delete, beginClo, deptId, bAccCode, itemCode, sessionMap, style, true, dataList, reportCondition);

                            int total = StringUtils.getObjInt(resultMap.get("total"));
                            BigDecimal sum = new BigDecimal(StringUtils.getObjStr(resultMap.get("sum")));


                            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                if (i == itemCodeList.size() - 1) { //针对最后一行
                                    ExcelUtil.removeRow(sheet, i + beginRow + delete);
                                }
                                delete--;
                            } else {
                                if (total != 0) { //如果total为零  则认为没有这一列
                                    HSSFCell cellSum = sheet.getRow(i + beginRow + delete).getCell(total);
                                    cellSum.setCellStyle(style);
                                    cellSum.setCellValue(sum.doubleValue());

                                    /*  统计第一行的合计值*/
                                    if (sessionMap.containsKey("sum" + total)) {
                                        sessionMap.put("sum" + total, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + total))).add(sum));
                                    } else {
                                        sessionMap.put("sum" + total, sum);
                                    }
                                }
                            }
                        }
                    } else {
                        ExcelUtil.removeRow(sheet, beginRow);
                    }
                    /*  给第一行的合计值 赋值*/
                    for (int i = beginClo; i < sheet.getRow(beginRow - 1).getPhysicalNumberOfCells(); i++) {
                        HSSFCell cellTotal = sheet.getRow(beginRow - 1).getCell(i);
                        cellTotal.setCellStyle(style);
                        cellTotal.setCellValue(new BigDecimal(StringUtils.getObjStrBigDeci(sessionMap.get("sum" + i))).doubleValue());//给合计赋值
                    }
                    sessionMap.clear();
                }
            }

            FileUtils.judeDirExists(folderPath);
            FileUtils.judeFileExists(excelPath);
            try (FileOutputStream out = new FileOutputStream(excelPath)) {
                workBook.write(out); //写回数据
            }

            asynDown.setStatus(Constans.AsynDownStatus.PROCESSED_SUCC.getValue());
            asynDown.setMsg("处理成功");

        } catch (Exception e) {
            log.error("导出数据失败!", e);
            asynDown.setStatus(Constans.AsynDownStatus.PROCESSED_FAIL.getValue());
            asynDown.setMsg(e.toString());
        } finally {
            asynDownService.updateFile(asynDown);
        }

    }

    /**
     * @param sheet           工作sheet
     * @param beginRow        操作开始行
     * @param beginClo        操作开始列
     * @param deptName        用户所属机构名
     * @param bAccCode
     * @param sessionMap      缓存Map
     * @param style           excel格式
     * @param IsCache         是否缓存
     * @param dataList        查询的数据
     * @param reportCondition 首页筛选条件
     * @return
     */
    private Map getCellVal(HSSFSheet sheet, int beginRow, int beginClo, String deptName, String bAccCode, String itemCode, Map sessionMap, CellStyle style, boolean IsCache, List<ReportRsp> dataList, ReportCondition reportCondition) {

        Map resMap = new HashMap();//返回结果集
        BigDecimal sum = new BigDecimal("0.00");
        int total = 0;//合计的列

        for (int j = beginClo; j < sheet.getRow(beginRow).getPhysicalNumberOfCells(); j++) {
            BigDecimal bigDecimal = new BigDecimal("0.00"); //统计符合列的数据

            //HSSFCell cellgetVal = sheet.getRow( beginRow).getCell(j);//使用第一行的值
            HSSFCell cellsetVal = sheet.getRow(beginRow).getCell(j);//
            cellsetVal.setCellStyle(style);
            // cellsetVal.setCellType(CellType.NUMERIC);//将值设置为数字格式
            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
            if (IsCache) {
                //将首行的值存到session,
                if (sessionMap.containsKey(String.valueOf(j))) {
                    cellVal = StringUtils.getObjStr(sessionMap.get(String.valueOf(j)));
                } else {
                    sessionMap.put(String.valueOf(j), cellVal);
                }
            }

            /*替换可能存在的中文字符*/
            cellVal.replace("（", "(");
            cellVal.replace("）", ")");
            cellVal.replace("，", ",");
            cellVal.replace("：", ":");
            cellVal.replace("，", ",");

            /*如果是空格 忽略*/
            if (StringUtils.isEmpty(cellVal)) {
                continue;
            }
            /*如果是 一 则忽略*/
            if (cellVal.equalsIgnoreCase("\\一")) {
                continue;
            }
            if (cellVal.equalsIgnoreCase("sum")) {//代表合计
                total = j;//记录合计的列号
                continue;//不往下执行
            }
            // if (cellVal.contains("*")) {
            //     cellVal = cellVal.substring(0, cellVal.length() - 1);
            //     String FTempResult = reportService.getDataByAccCode(cellVal);
            //     cellsetVal.setCellValue(FTempResult);
            //     continue;
            // }
            if (cellVal.contains("+") && cellVal.contains("{")) {
                String[] split = cellVal.split("\\+");
                BigDecimal temp = new BigDecimal("0.00");
                for (String s : split) {
                    BigDecimal dateForJson = getDateForJson(s, j, sessionMap, bAccCode, itemCode, deptName, reportCondition);
                    temp = temp.add(dateForJson);
                }
                cellsetVal.setCellValue(temp.doubleValue());
                sum = sum.add(temp);
                continue;
            }
            if (cellVal.contains("{")) {//代表是json
                BigDecimal dateForJson = getDateForJson(cellVal, j, sessionMap, bAccCode, itemCode, deptName, reportCondition);
                cellsetVal.setCellValue(dateForJson.doubleValue());
                sum = sum.add(dateForJson);//将数量加到合计中
                continue;
            }

            /**
             * 获取要小计的列数
             */
            if (cellVal.contains("(")) { //代表小计
                Map dateSum = getDateSum(cellVal, j, sessionMap, beginRow, bAccCode, itemCode, sheet, dataList, cellsetVal, deptName, reportCondition);
                if (dateSum != null && dateSum.size() > 0) {
                    j = StringUtils.getObjInt(dateSum.get("index"));
                    sum = sum.add(new BigDecimal(StringUtils.getObjStr(dateSum.get("subtotal"))));
                }
                continue;
            }

            /*值为空则设置为0*/
            if (StringUtils.isEmpty(cellVal)) {
                cellsetVal.setCellValue(bigDecimal.doubleValue());
            } else {
                if (dataList != null && dataList.size() > 0) {
                    String[] split = cellVal.split("\\+");
                    for (int k = 0; k < split.length; k++) {
                        for (ReportRsp reportRsp : dataList) {
                            String accCode = reportRsp.getAccCode();
                            if (accCode.length() >= split[k].length()) {
                                if (split[k].equalsIgnoreCase(accCode.substring(0, split[k].length()))) {
                                    bigDecimal = bigDecimal.add(new BigDecimal(reportRsp.getCrAmt()));
                                }
                            }
                        }
                    }
                    cellsetVal.setCellValue(bigDecimal.doubleValue());
                    sum = sum.add(bigDecimal);
                } else {
                    cellsetVal.setCellValue(bigDecimal.doubleValue());
                }
            }
            if (IsCache) {
                /* 统计第一行的合计值*/
                if (sessionMap.containsKey(("sum" + j))) {
                    sessionMap.put("sum" + j, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + j))).add(bigDecimal));
                } else {
                    sessionMap.put("sum" + j, bigDecimal);
                }
            }
        }
        resMap.put("total", total);
        resMap.put("sum", sum.toString());
        return resMap;
    }

    /**
     * 获取json的方法
     *
     * @param cellVal         当前clo的值
     * @param index           当前列的索引
     * @param sessionMap      缓存Map
     * @param bAccCode        当前的模板值
     * @param reportCondition 首页筛选条件
     * @return 返回这一列的值
     */
    private BigDecimal getDateForJson(String cellVal, int index, Map sessionMap, String bAccCode, String itemCode, String deptName, ReportCondition reportCondition) {
        /*bean转成map*/
        Map<String, Object> beanMap = Map2Bean.transBean2Map(reportCondition);

        Map<String, Object> resMap = null;
        BigDecimal bigDecimal2 = new BigDecimal("0.00"); //统计符合列的数据
        //将首行的值存到session,
        if (sessionMap.containsKey("temp" + index)) {
            resMap = (Map) sessionMap.get("temp" + index);
        } else {
            /*转成map*/
            Map<String, Object> paramMap = StringUtils.getStringToMap(cellVal);
            paramMap.putAll(beanMap);
            // paramMap.put("deptName", deptName);
            // paramMap.put("itemCode", itemCode);
            if (!StringUtils.isEmpty(itemCode)) {
                paramMap.put("isItemCode", true);
            } else {
                paramMap.put("isItemCode", false);
            }
            resMap = reportService.getDateByCondition(paramMap);
            sessionMap.put("temp" + index, resMap);
        }

        BigDecimal colVal = null;
        if (!StringUtils.isEmpty(itemCode)) {
            if (resMap.containsKey(bAccCode + "/" + itemCode)) {
                colVal = new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode + "/" + itemCode))).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                colVal = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        } else {
            if (resMap.containsKey(bAccCode)) {
                colVal = new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode))).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                colVal = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }

          /*
            统计第一行的合计值
             */
        if (sessionMap.containsKey("sum" + index)) {
            sessionMap.put("sum" + index, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + index))).add(colVal));
        } else {
            sessionMap.put("sum" + index, bigDecimal2);
        }
        return colVal;
    }

    /**
     * @param cellVal         当前clo的值
     * @param index           当前列的索引
     * @param sessionMap      缓存Map
     * @param begin           开始的行数
     * @param bAccCode        当前的模板值
     * @param sheet           工作sheet
     * @param dataList        查询的数据
     * @param cellsetVal      设置值
     * @param reportCondition 首页筛选条件
     */
    private Map getDateSum(String cellVal, int index, Map sessionMap, int begin, String bAccCode, String itemCode, HSSFSheet sheet, List<ReportRsp> dataList, HSSFCell cellsetVal, String deptName, ReportCondition reportCondition) {
        /*bean转成map*/
        Map<String, Object> beanMap = Map2Bean.transBean2Map(reportCondition);

        Map resMap = new HashMap();
        String splitFirst = "";//是否是sum
        String str = "";//获取()内的数据
        if (cellVal.split("\\(").length > 1) {
            splitFirst = cellVal.split("\\(")[0];
            //说明是 ( 这个符号(英文)
            //获取里面的值
            int start = cellVal.indexOf("(") + 1;//截取开始值
            int end = cellVal.indexOf(")");//截取结束值
            str = cellVal.substring(start, end);//获取()内的数据
        } else {
            return resMap;
        }
        int size = 0;//小计长度
        if (str.split(",").length > 1) {
            size = StringUtils.getObjInt(str.split(",")[1]) - StringUtils.getObjInt(str.split(",")[0]) + 1;
        } else {
            return resMap;
        }

        if (splitFirst.equalsIgnoreCase("sum")) {//代表小计
            size += index;//小计的长度
            BigDecimal subtotal = new BigDecimal("0.00");//小计金额

            /**
             * 小计这段的数据的操作
             */
            for (int k = index + 1; k < size + 1; k++) {
                HSSFCell cell2setValue = sheet.getRow(begin).getCell(k);
                String cellVal2 = ExcelUtil.getStringValueFromCell(cell2setValue);//获取列的内容
                cell2setValue.setCellStyle(cellsetVal.getCellStyle());
                // cell2setValue.setCellType(CellType.NUMERIC);//将值设置为数字格式
                // if()
                //将首行的值存到session,
                if (sessionMap.containsKey(String.valueOf(k))) {
                    cellVal2 = StringUtils.getObjStr(sessionMap.get(String.valueOf(k)));
                } else {
                    sessionMap.put(String.valueOf(k), cellVal2);
                }

                /*替换可能存在的中文字符*/
                cellVal2.replace("（", "(");
                cellVal2.replace("）", ")");
                cellVal2.replace("，", ",");
                cellVal2.replace("：", ":");
                cellVal2.replace("，", ",");

                /*如果是 一 则忽略*/
                if (cellVal2.equalsIgnoreCase("\\一")) {
                    continue;
                }
                if (cellVal2.contains("+") && cellVal2.contains("{")) {
                    String[] split = cellVal2.split("\\+");
                    BigDecimal temp = new BigDecimal("0.00");
                    for (String s : split) {
                        BigDecimal dateForJson = getDateForJson(s, k, sessionMap, bAccCode, itemCode, deptName, reportCondition);
                        temp = temp.add(dateForJson);
                    }
                    cell2setValue.setCellValue(temp.doubleValue());
                    subtotal = subtotal.add(temp);
                    continue;
                }
                if (cellVal2.contains("{")) {//代表是json
                    BigDecimal dateForJson = getDateForJson(cellVal2, k, sessionMap, bAccCode, itemCode, deptName, reportCondition);
                    cell2setValue.setCellValue(dateForJson.doubleValue());
                    subtotal = subtotal.add(dateForJson);
                    //sum = sum.add(dateForJson);//将数量加到合计中
                    continue;
                }

                /**
                 * 获取要小计的列数
                 */
                if (cellVal2.contains("(")) { //代表小计
                    Map dateSum = getDateSum(cellVal2, k, sessionMap, begin, bAccCode, itemCode, sheet, dataList, cell2setValue, deptName, reportCondition);
                    if (dateSum != null && dateSum.size() > 0) {
                        k = StringUtils.getObjInt(dateSum.get("index"));
                        subtotal = subtotal.add(new BigDecimal(StringUtils.getObjStr(dateSum.get("subtotal"))));
                    }
                    continue;
                }

                BigDecimal bigDecimal2 = new BigDecimal("0.00"); //统计符合列的数据
                /*值为空则设置为0*/
                if (StringUtils.isEmpty(cellVal2)) {
                    cell2setValue.setCellValue(bigDecimal2.doubleValue());
                } else {
                    if (dataList != null && dataList.size() > 0) {
                        String[] split2 = cellVal2.split("\\+");
                        for (int l = 0; l < split2.length; l++) {
                            for (ReportRsp reportRsp : dataList) {
                                String accCode = reportRsp.getAccCode();
                                if (accCode.length() >= split2[l].length()) {
                                    if (split2[l].equalsIgnoreCase(accCode.substring(0, split2[l].length()))) {
                                        bigDecimal2 = bigDecimal2.add(new BigDecimal(reportRsp.getCrAmt()));
                                    }
                                }
                            }
                        }
                        cell2setValue.setCellValue(bigDecimal2.doubleValue());
                        subtotal = subtotal.add(bigDecimal2);
                    } else {
                        cell2setValue.setCellValue(bigDecimal2.doubleValue());
                    }
                }
                  /*
                    统计第一行的合计值
                     */
                if (sessionMap.containsKey("sum" + k)) {
                    sessionMap.put("sum" + k, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + k))).add(bigDecimal2));
                } else {
                    sessionMap.put("sum" + k, bigDecimal2);
                }
            }
          /*
                统计第一行的合计值
                 */
            if (sessionMap.containsKey("sum" + index)) {
                sessionMap.put("sum" + index, new BigDecimal(StringUtils.getObjStr(sessionMap.get("sum" + index))).add(subtotal));
            } else {
                sessionMap.put("sum" + index, subtotal);
            }
            resMap.put("subtotal", subtotal);
            // sum = sum.add(subtotal);//将数量加到合计中
            //resMap.put("sum", sum);
            cellsetVal.setCellValue(subtotal.doubleValue());//将小计的值赋值
            index = size;
            resMap.put("index", index);
            return resMap; //不往下执行
        } else {
            return resMap;
        }
    }
}


