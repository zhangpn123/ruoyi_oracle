package com.ruoyi.project.report.report.controller;

import com.ruoyi.common.utils.PoiUtil;
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
import net.sf.ehcache.search.aggregator.Sum;
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
import javax.servlet.http.HttpSession;
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

    @Autowired
    private HttpServletRequest request;

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

        HttpSession session = request.getSession();//使用session

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
                        && !sheetName.equalsIgnoreCase("Z07 一般公共预算财政拨款收入支出决算表(财决07表)")
                ) {
                    continue;
                }

                List<Map<String, Object>> bAccCodeList = reportService.getBAccCode();
                int beginRow = 9;
                int delete = 0; //是否偶有删除的行
                if (bAccCodeList != null && bAccCodeList.size() > 0) {
                    /*遍历表中的行的*/
                    for (int i = 0; i < bAccCodeList.size(); i++) {
                        BigDecimal sum = new BigDecimal("0.00");
                        /*获取baccCode*/
                        // String bAccCode = ExcelUtil.getStringValueFromCell(sheet.getRow(i).getCell(0));
                        String bAccCode = StringUtils.getObjStr(bAccCodeList.get(i).get("bAccCode"));
                        //String bAccName = StringUtils.getObjStr(bAccCodeList.get(i).get("bAccName"));

                        if (StringUtils.isEmpty(bAccCode)) {
                            continue;
                        }

                        sheet.getRow(i + beginRow + delete).getCell(0).setCellValue(bAccCode);
                        //sheet.getRow(i + beginRow+ delete).getCell(3).setCellValue(bAccName);

                        Map paramsMap = new HashMap();
                        paramsMap.put("bAccCode", bAccCode);
                        paramsMap.put("deptName", z03Report.getDeptName());
                        List<ReportRsp> dataList = reportService.getData(paramsMap);

                        int total = 0;//合计的列

                        /*遍历表中的列*/
                        for (int j = 4; j < sheet.getRow(i + beginRow + delete).getPhysicalNumberOfCells(); j++) {
                            BigDecimal bigDecimal = new BigDecimal("0.00"); //统计符合列的数据

                            //HSSFCell cellgetVal = sheet.getRow( beginRow).getCell(j);//使用第一行的值
                            HSSFCell cellsetVal = sheet.getRow(i + beginRow + delete).getCell(j);//
                            String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal);//获取列的内容
                            //将首行的值存到session,
                            if (i + beginRow == 9) {
                                session.setAttribute(String.valueOf(j), cellVal);
                            } else {
                                cellVal = session.getAttribute(String.valueOf(j)).toString();
                            }
                            /*替换可能存在的中文字符*/
                            cellVal.replace("（", "(");
                            cellVal.replace("）", ")");
                            cellVal.replace("，", ",");
                            cellVal.replace("：", ":");
                            cellVal.replace("，", ",");

                            /*如果是 一 则忽略*/
                            if (cellVal.equalsIgnoreCase("\\一")) {
                                continue;
                            }
                            if (cellVal.equalsIgnoreCase("sum")) {//代表合计
                                total = j;//记录合计的列号
                                continue;//不往下执行
                            }
                            if (cellVal.contains("+") && cellVal.contains("{")) {
                                String[] split = cellVal.split("\\+");
                                BigDecimal temp = new BigDecimal("0.00");
                                for (String s : split) {
                                        BigDecimal dateForJson = getDateForJson(s, j, session, i + beginRow, bAccCode);
                                        temp = temp.add(dateForJson);
                                }
                                cellsetVal.setCellValue(temp.toString());
                                sum = sum.add(temp);
                                continue;
                            }
                            if (cellVal.contains("{")) {//代表是json
                                BigDecimal dateForJson = getDateForJson(cellVal, j, session, i + beginRow, bAccCode);
                                cellsetVal.setCellValue(dateForJson.toString());
                                sum = sum.add(dateForJson);//将数量加到合计中
                                continue;
                            }

                            /**
                             * 获取要小计的列数
                             */
                            if (cellVal.contains("(")) { //代表小计
                                Map dateSum = getDateSum(cellVal, j, session, i + beginRow, bAccCode, sheet, sum, delete, dataList, cellsetVal);
                                if (dateSum != null && dateSum.size()>0){
                                    j = StringUtils.getObjInt(dateSum.get("index"));
                                    sum = sum.add( new BigDecimal(StringUtils.getObjStr(dateSum.get("sum"))));
                                }
                                continue;
                            }

                            /*值为空则设置为0*/
                            if (StringUtils.isEmpty(cellVal)) {
                                cellsetVal.setCellValue(bigDecimal.toString());
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
                                    cellsetVal.setCellValue(bigDecimal.toString());
                                } else {
                                    cellsetVal.setCellValue(bigDecimal.toString());
                                }
                                /* 统计第一行的合计值*/
                                if (i + beginRow == 9) {
                                    session.setAttribute("sum" + j, bigDecimal);
                                } else {
                                    session.setAttribute("sum" + j, new BigDecimal(session.getAttribute("sum" + j).toString()).add(bigDecimal));
                                }
                                sum = sum.add(bigDecimal);
                            }
                        }
                        if (total != 0) { //如果total为零  则认为没有这一列
                            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                                if (i == bAccCodeList.size() - 1) {
                                    ExcelUtil.removeRow(sheet, i + beginRow + delete);
                                    // sheet.removeRow(sheet.getRow(i + beginRow + delete));
                                }
                                delete--;
                            } else {
                                sheet.getRow(i + beginRow + delete).getCell(total).setCellValue(sum.toString());
                            }
                            /*  统计第一行的合计值*/
                            if (i + beginRow == 9) {
                                session.setAttribute("sum" + total, sum);
                            } else {
                                session.setAttribute("sum" + total, new BigDecimal(session.getAttribute("sum" + total).toString()).add(sum));
                            }
                        }

                    }
                }
                /*  给第一行的合计值 赋值*/
                for (int i = 4; i < sheet.getRow(beginRow - 1).getPhysicalNumberOfCells(); i++) {
                    sheet.getRow(beginRow - 1).getCell(i).setCellValue(session.getAttribute("sum" + i).toString());//给合计赋值
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
            // session.invalidate();//销毁session
        }

    }


    /**
     * 获取json的方法
     * @param cellVal 当前clo的值
     * @param index 当前列的索引
     * @param session
     * @param begin 开始的行数
     * @param bAccCode 当前的模板值
     * @return 返回这一列的值
     */
    public BigDecimal getDateForJson(String cellVal, int index, HttpSession session, int begin, String bAccCode) {
        Map<String, Object> resMap = null;
        //将首行的值存到session,
        if (begin == 9) {
            /*转成map*/
            Map<String, Object> paramMap = StringUtils.getStringToMap(cellVal);
            resMap = reportService.getDateByCondition(paramMap);
            session.setAttribute(String.valueOf(index), resMap);
        } else {
            resMap = (Map) session.getAttribute(String.valueOf(index));
        }

        BigDecimal colVal = null;
        if (resMap.containsKey(bAccCode)) {
            colVal = new BigDecimal(StringUtils.getObjStr(resMap.get(bAccCode))).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            colVal = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return colVal;
    }

    /**
     * @param cellVal 当前clo的值
     * @param index 当前列的索引
     * @param session
     * @param begin 开始的行数
     * @param bAccCode 当前的模板值
     * @param sheet
     * @param sum 合计的值
     * @param delete 删除的行数
     * @param dataList 查询的数据
     * @param cellsetVal 设置值
     */
    public Map getDateSum(String cellVal, int index, HttpSession session, int begin, String bAccCode, HSSFSheet sheet, BigDecimal sum, int delete, List<ReportRsp> dataList, HSSFCell cellsetVal) {
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
                HSSFCell cell2setValue = sheet.getRow(begin + delete).getCell(k);
                String cellVal2 = ExcelUtil.getStringValueFromCell(cell2setValue);//获取列的内容
                // if()
                //将首行的值存到session,
                if (begin == 9) {
                    session.setAttribute(String.valueOf(k), cellVal2);
                } else {
                    cellVal2 = session.getAttribute(String.valueOf(k)).toString();
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
                        BigDecimal dateForJson = getDateForJson(s, k, session, begin, bAccCode);
                        temp = temp.add(dateForJson);
                    }
                    cell2setValue.setCellValue(temp.toString());
                    sum = sum.add(temp);
                    continue;
                }
                if (cellVal2.contains("{")) {//代表是json
                    BigDecimal dateForJson = getDateForJson(cellVal2, k, session, begin, bAccCode);
                    cell2setValue.setCellValue(dateForJson.toString());
                    sum = sum.add(dateForJson);//将数量加到合计中
                    continue;
                }

                /**
                 * 获取要小计的列数
                 */
                if (cellVal2.contains("(")) { //代表小计
                    Map dateSum = getDateSum(cellVal2, k, session, begin, bAccCode, sheet, sum, delete, dataList, cell2setValue);
                    if (dateSum != null && dateSum.size()>0){
                        k = StringUtils.getObjInt(dateSum.get("index"));
                        sum = sum.add( new BigDecimal(StringUtils.getObjStr(dateSum.get("sum"))));
                    }
                    continue;
                }

                BigDecimal bigDecimal2 = new BigDecimal("0.00"); //统计符合列的数据
                /*值为空则设置为0*/
                if (StringUtils.isEmpty(cellVal2)) {
                    cell2setValue.setCellValue(bigDecimal2.toString());
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
                        cell2setValue.setCellValue(bigDecimal2.toString());
                    } else {
                        cell2setValue.setCellValue(bigDecimal2.toString());
                    }
                    /*
                    统计第一行的合计值
                     */
                    if (begin == 9) {
                        session.setAttribute("sum" + k, bigDecimal2);
                    } else {
                        session.setAttribute("sum" + k, new BigDecimal(session.getAttribute("sum" + k).toString()).add(bigDecimal2));
                    }
                    subtotal = subtotal.add(bigDecimal2);
                }
            }
          /*
                统计第一行的合计值
                 */
            if (begin == 9) {
                session.setAttribute("sum" + index, subtotal);
            } else {
                session.setAttribute("sum" + index, new BigDecimal(session.getAttribute("sum" + index).toString()).add(subtotal));
            }
            // resMap.put("subtotal",subtotal);
            sum = sum.add(subtotal);//将数量加到合计中
            resMap.put("sum",sum);
            cellsetVal.setCellValue(subtotal.toString());//将小计的值赋值
            index = size;
            resMap.put("index",index);
            return resMap; //不往下执行
        } else {
            return resMap;
        }
    }
}

