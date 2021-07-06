package com.ruoyi.project.report.main.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.UploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.BaseResp;
import com.ruoyi.framework.web.page.Response;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.dto.ReportCondition;
import com.ruoyi.project.report.dto.ResponseMain;
import com.ruoyi.project.report.main.service.MainService;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.dept.service.IDeptService;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: ruoyi_oracle
 * @description: 首页展示
 * @author: authorName
 * @create: 2021-04-04 21:52
 **/
@RequestMapping("/report/main")
@Controller
@Slf4j
public class MainController extends BaseController {
    @Autowired
    private IDictTypeService dictTypeService;
    @Autowired
    private IDeptService iDeptService;
    @Autowired
    private IDictDataService dictDataService;
    @Autowired
    private MainService mainService;
    @Autowired
    private IDeptService deptService;


    @PostMapping("/list")
    @ResponseBody
    public BaseResp<List<ResponseMain>> list(ReportCondition reportCondition) {
        log.info("开始查询首页报表数据 " );

        // List<ResponseMain> list2 = new ArrayList<>();
        // ResponseMain responseMain1 = new ResponseMain();
        // responseMain1.setName("客户1");
        // responseMain1.setCode("0001");
        // responseMain1.setValue("121212");
        // list2.add(responseMain1);
        // ResponseMain responseMain2 = new ResponseMain();
        // responseMain2.setName("客户2");
        // responseMain2.setCode("0002");
        // responseMain2.setValue("221212");
        // list2.add(responseMain2);
        // ResponseMain responseMain3 = new ResponseMain();
        // responseMain3.setName("客户3");
        // responseMain3.setCode("0003");
        // responseMain3.setValue("321212");
        // list2.add(responseMain3);
        // return Response.ok(list2);

        /*获取表头信息*/
        dictTypeService.clearCache();//清楚缓存
        List<DictData> dictData = dictTypeService.selectDictDataByType(reportCondition.getDictDataype());
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

        List<ResponseMain> list = mainService.selectData(fieldList, replaceMap, reportCondition);
        if(reportCondition.getDictDataype().equalsIgnoreCase("report_main_column")){
            ResponseMain other = new ResponseMain();
            BigDecimal total = new BigDecimal("0");
            Iterator<ResponseMain> it = list.iterator();
            while (it.hasNext()) {
                ResponseMain responseMain = it.next();
                if ("0.00".equalsIgnoreCase(responseMain.getValue())) {
                    it.remove();
                } else {
                    for (DictData dictDatum : dictData) {
                        if (responseMain.getCode().equals(dictDatum.getDictValue())) {
                            responseMain.setName(dictDatum.getDictLabel());
                            if (dictDatum.getDictLabel().contains("/")) {
                                total = total.add(new BigDecimal(responseMain.getValue()));
                                it.remove();
                            }
                        }
                    }
                }
            }
            if (!"0".equalsIgnoreCase(total.toString())){
                other.setName("其他收入");
                other.setCode("other");
                other.setValue(total.toString());
                list.add(other);
            }
        }else{
            Iterator<ResponseMain> it = list.iterator();
            while (it.hasNext()){
                ResponseMain responseMain = it.next();
                if("0.00".equalsIgnoreCase(responseMain.getValue())){
                    it.remove();
                }else{
                    for (DictData dictDatum : dictData) {
                        if (responseMain.getCode().equals(dictDatum.getDictValue())) {
                            responseMain.setName(dictDatum.getDictLabel());
                        }
                    }
                }
            }
        }

        return Response.ok(list);
    }


    @PostMapping("/detail")
    @ResponseBody
    public TableDataInfo detail(ReportCondition reportCondition) {
        log.info("开始查询首页报表明细数据 {}",reportCondition);

        if (StringUtils.isEmpty(reportCondition.getDeptId())) {
            /*取当前用户的所属部门*/
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            Dept dept = iDeptService.selectDeptById(user.getDeptId());
            reportCondition.setDeptId(user.getDeptId());
            reportCondition.setDeptName(dept.getDeptName());
        }

        /*存储表头信息*/
        List<String> fieldList = new LinkedList<>();
        /*需要合并的数据信息*/
        List<Map<String, Object>> replaceMap = new ArrayList<>();

        //结果集
        List<Map<String, Object>> list = new ArrayList<>();
        /*获取表头信息*/
        if (StringUtils.isEmpty(reportCondition.getAccCode())) {
            dictTypeService.clearCache();//清楚缓存
            List<DictData> dictData = dictTypeService.selectDictDataByType(reportCondition.getDictDataype());

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

            List<ResponseMain> responseMains = mainService.selectData(fieldList, replaceMap, reportCondition);
            if(reportCondition.getDictDataype().equalsIgnoreCase("report_main_column")){
                ResponseMain other = new ResponseMain();
                BigDecimal total = new BigDecimal("0");
                Iterator<ResponseMain> it = responseMains.iterator();
                while (it.hasNext()){
                    ResponseMain responseMain = it.next();
                    for (DictData dictDatum : dictData) {
                        if (responseMain.getCode().equals(dictDatum.getDictValue())) {
                            responseMain.setName(dictDatum.getDictLabel());
                            if (dictDatum.getDictLabel().contains("/")){
                                total = total.add(new BigDecimal(responseMain.getValue()));
                                it.remove();
                            }
                        }
                    }
                }
                if (!"0".equalsIgnoreCase(total.toString())){
                    other.setName("其他收入");
                    other.setCode("other");
                    other.setValue(total.toString());
                    responseMains.add(other);
                }
            }else{
                for (ResponseMain responseMain : responseMains) {
                    for (DictData dictDatum : dictData) {
                        if (responseMain.getCode().equals(dictDatum.getDictValue())) {
                            responseMain.setName(dictDatum.getDictLabel());
                        }
                    }
                }
            }

            for (ResponseMain responseMain : responseMains) {
                if(!"0.00".equalsIgnoreCase(responseMain.getValue())){
                    Map map1 = new HashMap();
                    map1.put("coName",reportCondition.getDeptName());
                    map1.put("accName",responseMain.getName());
                    map1.put("crAmt",responseMain.getValue());
                    list.add(map1);
                }
            }

        } else {
            fieldList.add(reportCondition.getAccCode());
            list = mainService.selectDataDetail(fieldList, replaceMap, reportCondition);
            List<Dept> deptList = deptService.selectDeptIdList();
            Iterator<Map<String, Object>> it = list.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = it.next();
                if ("0.00".equalsIgnoreCase(map.get("crAmt").toString())) {
                    it.remove();
                } else {
                    for (Dept dept : deptList) {
                        if (dept.getDeptId().equalsIgnoreCase(map.get("coCode").toString())) {
                            map.put("coName", dept.getDeptName());
                        }
                    }
                    map.put("accName", reportCondition.getAccName());
                }
            }
        }
        return getDataTable(list);
    }

    @GetMapping("/detailed/{params}")
    public String detailed(@PathVariable("params") String params, ModelMap mmap) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        ReportCondition reportCondition = JSONObject.toJavaObject(jsonObject, ReportCondition.class);
        mmap.put("reportCondition", reportCondition);
        return "tool/main/detailed";
    }

    /**
     * 报表模板导入
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Log(title = "报表统计", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importExportTemp(MultipartFile file) {
        //将下载模板存放到本地
        String fileName = "MainReportTemp.xls";
        String exportTempPath = RuoYiConfig.getMainReportPath() + fileName;
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
    @PostMapping("/report")
    public AjaxResult report(ReportCondition reportCondition, HttpServletResponse response, HttpServletRequest request) {
        log.info("开始导出首页报表数据");
        //获取数据
        /*获取表头信息*/
        dictTypeService.clearCache();//清楚缓存
        List<DictData> dictData = dictTypeService.selectDictDataByType("report_main_column");
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

        List<ResponseMain> list = mainService.selectData(fieldList, replaceMap, reportCondition);


        // 获取模板路径
        String fileName = "MainReportTemp.xls";
        String filePath = RuoYiConfig.getMainReportPath() + fileName;

        String outFileName = "收入表" + System.currentTimeMillis() + ".xls";
        String downloadPath = RuoYiConfig.getDownloadPath() + outFileName;
        try (FileInputStream fis = new FileInputStream(filePath)) {
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
            //获取对应的sheet
            HSSFSheet sheet = workBook.getSheetAt(0);
            String sheetName = sheet.getSheetName();
            log.info("开始处理{}表", sheetName);

            DictData dict = new DictData();
            dict.setDictLabel(sheetName);
            dict = dictDataService.selectByDictLaber(dict);
            String dictValue = dict.getDictValue();
            int beginRow = StringUtils.getObjInt(dictValue.split("/")[0]);
            int beginClo = StringUtils.getObjInt(dictValue.split("/")[1]);//开始写的第一列数据
            for (int j = beginClo; j < sheet.getRow(beginRow).getPhysicalNumberOfCells(); j++) {
                HSSFCell cellsetVal = sheet.getRow(beginRow).getCell(j);//
                String cellVal = ExcelUtil.getStringValueFromCell(cellsetVal).trim();//获取列的内容
                for (ResponseMain responseMain : list) {
                    if (responseMain.getCode().equals(cellVal)) {
                        cellsetVal.setCellStyle(style);
                        cellsetVal.setCellValue(new BigDecimal(StringUtils.getObjStrBigDeci(responseMain.getValue())).doubleValue());//给合计赋值
                    }
                }
            }
            FileUtils.judeFileExists(downloadPath);
            try (FileOutputStream out = new FileOutputStream(downloadPath)) {
                workBook.write(out); //写回数据
            }
            return AjaxResult.success(outFileName);
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
        return AjaxResult.error("导出失败");
    }

}
