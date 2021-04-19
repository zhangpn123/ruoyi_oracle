package com.ruoyi.project.revice.analysis.controller;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import com.ruoyi.project.report.customquert.service.CustomqueryService;
import com.ruoyi.project.revice.analysis.service.AnalysisService;
import com.ruoyi.project.revice.dto.Report;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.dept.service.IDeptService;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-01-11 15:21
 **/
@Controller
@RequestMapping("/review/analysis")
@Slf4j
public class AnalysisController extends BaseController {

    private String prefix = "review";

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private IDictDataService dictDataService;

    @Autowired
    private CustomqueryService customqueryService;

    @Autowired
    private IDeptService deptService;

    @RequiresPermissions("review:analysis:view")
    @GetMapping()
    public String analysis() {
        return prefix + "/analysis";
    }

    // @RequiresPermissions("review:role:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(CustomPageReq customPageReq) {
        startPage();
        List<Report> list = analysisService.selectList(customPageReq);
        return getDataTable(list);
    }

    /**
     * 查询明细
     */
    // @RequiresPermissions("/review/analysis")
    @GetMapping("/detailed/{coCode}")
    public String detailed(@PathVariable("coCode") String coCode, ModelMap mmap) {
        mmap.put("report", analysisService.selectReport(coCode));
        return prefix + "/detailed";
    }

    /**
     * 查询明细
     */
    // @RequiresPermissions("system:role:list")
    @PostMapping("/detailedList")
    @ResponseBody
    public TableDataInfo detailedList(Report report) {
        startPage();
        List<Report> list = analysisService.selectDetailedList(report);
        return getDataTable(list);
    }

    /**
     * 运行所有规范性查询的sql并把数据保存到统计表
     */
    @RequiresPermissions("review:analysis:report")
    @PostMapping("/report")
    @ResponseBody
    public AjaxResult report(CustomPageReq customPageReq) {
       /*获取所有的机构信息*/
        List<Dept> deptList = deptService.selectDeptIdList();

        /*获取所有的规范查询脚本数据*/
        List<DictData> dictDataList = dictDataService.selectAllByDictType("normative_verification");
        /*待存入数据*/
        List<Report> params = new ArrayList<>();
        /*执行所有的查询*/
        for (Dept dept : deptList) {
            customPageReq.setDeptId(dept.getDeptId());
            for (DictData dictData : dictDataList) {
                String sql = getSql(dictData.getDictValue(), customPageReq);
                List<Map<String, Object>> customResult = customqueryService.selectList(sql);
                for (Map<String, Object> map : customResult) {
                    Report report = new Report();
                    report.setCoCode(map.get("CO_CODE").toString());
                    report.setQueDesc(map.get("ERROR_REASON").toString());
                    report.setVoucherNo(map.get("VOU_NO").toString());
                    report.setRemark(map.get("DESCPT").toString());
                    report.setRecord(map.get("VOU_SEQ").toString());
                    report.setAssCheck(map.get("VOU_DETAIL_SEQ").toString());
                    report.setQueType(map.get("ERROR_TYPE").toString());
                    report.setQueNum(dictData.getCssClass());
                    report.setCreateDate(DateUtils.curDateTime());
                    params.add(report);
                }
            }
        }
        /*将数据新增到表中*/
        analysisService.batchInsert(params);
        return toAjax(true);
    }


    @RequiresPermissions("review:analysis:export")
    @RequestMapping("/export")
    @ResponseBody
    public AjaxResult exportReport(CustomPageReq customPageReq) throws IOException {
        log.info("Start generating reports");
        //构建Excel
        String fileName = "预警监控信息分析"+ System.currentTimeMillis() + ".xls";
        String excelPath = RuoYiConfig.getDownloadPath() + fileName;
        //构建excel数据
        /*获取表头信息*/
        // dictTypeService.clearCache();//清除缓存
        // List<DictData> dictData = dictTypeService.selectDictDataByType("report_z03_column");
        // /*存储表头信息*/
        // LinkedList<String> fieldList = new LinkedList<>();
        // LinkedList<String> titleList = new LinkedList<>();
        //
        // /*需要合并的数据信息*/
        // List<Map<String, Object>> replaceMap = new ArrayList<>();
        // Map<String, Object> map = new HashMap<>();
        //
        // if (dictData != null && dictData.size() > 0) {
        //     for (DictData dictDatum : dictData) {
        //         String[] split = dictDatum.getDictValue().split("/");
        //         if (split.length > 1) {
        //             /*如果长度大于1就是有需要合并的数据*/
        //             map.put("dest", split[0]);//将要合并到的数据
        //
        //             List srctList = new ArrayList();
        //             for (int i = 1; i < split.length; i++) {
        //                 srctList.add(split[i]);
        //             }
        //             map.put("src", srctList);//需要合并的数据
        //             replaceMap.add(map);
        //             fieldList.add(split[0]);
        //         } else {
        //             fieldList.add(split[0]);
        //         }
        //         titleList.add(dictDatum.getDictLabel());
        //     }
        // }
        //
        //
        // LinkedList<Map<String, Object>> resultList = z03Service.selectRoleList(fieldList, replaceMap, reportCondition);
        // // titleList.addFirst("项目");
        // // titleList.addFirst("项目");
        // // fieldList.addFirst("bAccCode");
        // // fieldList.addFirst("bAccCode");
        // //构建excel数据
        // InputStream input = PoiUtil.getExcelFile(resultList, "Z03 收入决算表(财决03表)", titleList, fieldList);
        // ExcelUtil.writeExcel(input, excelPath);
        return AjaxResult.success(fileName);
    }


    /**
     * 替换表中的数据
     *
     * @param sql
     * @param customPageReq
     * @return
     */
    public String getSql(String sql, CustomPageReq customPageReq) {

        if (sql.contains("#jigou#")) {
            if (StringUtils.isEmpty(customPageReq.getDeptId())) {
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                sql = sql.replace("#jigou#", user.getDeptId());
            } else {
                sql = sql.replace("#jigou#", customPageReq.getDeptId());
            }
        }
        if (sql.contains("#nianfen#")) {
            if (StringUtils.isEmpty(customPageReq.getYear())) {
                sql = sql.replace("#nianfen#", DateUtils.getYear());
            } else {
                sql = sql.replace("#nianfen#", customPageReq.getYear());
            }
        }
        if (sql.contains("#yuefen#")) {
            if (StringUtils.isEmpty(customPageReq.getMonths())) {
                sql = sql.replace("#yuefen#", DateUtils.getMonth());
            } else {
                String[] split = customPageReq.getMonths().split(",");
                StringBuffer monthsSB = new StringBuffer();
                for (String s : split) {
                    monthsSB.append("'" + s + "',");
                }
                String sqlToDo = monthsSB.toString();
                /*去掉最后的, */
                String sqlFinish = sqlToDo.substring(0, sqlToDo.length() - 1);
                sql = sql.replace("'#yuefen#'", sqlFinish);
            }
        }
        return sql;
    }


}
