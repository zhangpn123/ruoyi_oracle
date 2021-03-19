package com.ruoyi.project.revice.analysis.controller;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import com.ruoyi.project.report.customquert.service.CustomqueryService;
import com.ruoyi.project.revice.analysis.service.AnalysisService;
import com.ruoyi.project.revice.dto.Report;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.role.domain.Role;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    // @RequiresPermissions("system:role:edit")
    @PostMapping("/report")
    @ResponseBody
    public AjaxResult report(CustomPageReq customPageReq) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        /*由于刚开始设计有问题  此处deptName 就是 deptId*/
        customPageReq.setDeptId(user.getDeptId());
        /*将此次查询的数据删除*/
        // analysisService.deleteAllBy
        /*根据统计时间判断表中是否已经存在 , 存在则删除重新统计*/
        /*获取所有的规范查询脚本数据*/
        List<DictData> dictDataList = dictDataService.selectAllByDictType("normative_verification");
        /*待存入数据*/
        List<Report> params = new ArrayList<>();
        /*执行所有的查询*/
        for (DictData dictData : dictDataList) {
            String sql = getSql(dictData.getDictValue(), customPageReq);
            List<Map<String, Object>> customResult = customqueryService.selectList(sql);
            for (Map<String, Object> map : customResult) {
                Report report = new Report();
                // report.setCoCode(customResult.get(""));
                params.add(report);
            }
        }
        /*将数据新增到表中*/
        analysisService.batchInsert(params);
        return toAjax(true);
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
