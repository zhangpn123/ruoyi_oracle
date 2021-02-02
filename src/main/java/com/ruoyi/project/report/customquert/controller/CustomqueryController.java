package com.ruoyi.project.report.customquert.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import com.ruoyi.project.report.customquert.service.CustomqueryService;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictDataService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: RuoYi
 * @description: 自定义查询
 * @author: authorName
 * @create: 2020-12-29 13:52
 **/

@Controller
@RequestMapping("report/custom")
@Slf4j
public class CustomqueryController extends BaseController {

    @Autowired
    private CustomqueryService customqueryService;

    @Autowired
    private IDictDataService dictDataService;

    private String prefix = "report/custom";

    /**
     * 展示页面
     *
     * @return
     */
    @RequiresPermissions("report:custom:view")
    @GetMapping()
    public String custom() {
        return prefix + "/custom";
    }

    @RequiresPermissions("report:custom:execute")
    @PostMapping("/getData")
    @ResponseBody
    public TableDataInfo getData(CustomPageReq customPageReq) {
        log.info("开始自定义分页查询");
        DictData dictData = dictDataService.selectDictDataById(Long.parseLong(customPageReq.getDictCode()));
        String customSql = dictData.getDictValue();
        customSql = getSql(customSql, customPageReq);
        startPage();
        List<Map<String, Object>> customResult = customqueryService.selectList(customSql);
        return getDataTable(customResult);
    }


    @PostMapping("/getHeaderList")
    @ResponseBody
    public TableDataInfo getHeaderList(CustomPageReq customPageReq) {
        log.info("开始获取查询的表头");
        /*校验数据*/
        TableDataInfo rspData = new TableDataInfo();
        if (StringUtils.isEmpty(customPageReq.getDictCode())) {
            log.error("要查询的sql条件为空");
            rspData.setCode(301);
            rspData.setMsg("请选择要查询的条件");
            return rspData;
        }
        DictData dictData = dictDataService.selectDictDataById(Long.parseLong(customPageReq.getDictCode()));
        if (dictData == null || StringUtils.isEmpty(dictData.getDictValue())) {
            log.error("要查询的数据为空");
            rspData.setCode(301);
            rspData.setMsg("查询的数据为空");
            return rspData;
        }
        String sql = dictData.getDictValue();
        sql = sql.toLowerCase();//转成小写
        if (!sql.startsWith("select")) {
            log.error("要查询的数据不是select开头");
            rspData.setCode(301);
            rspData.setMsg("查询的语句不合法：不是select开头的语句");
            return rspData;
        }
        try {
            sql = getSql(sql, customPageReq);
            PageHelper.startPage(1, 1, false);
            List<Map<String, Object>> customResult = customqueryService.selectList(sql);
            if (customResult != null && customResult.size() > 0) {
                LinkedHashMap custom = (LinkedHashMap) customResult.get(0);
                custom.remove("ROW_ID");
                Map customMap = new LinkedHashMap();
                customMap.put("ROW_ID", "ROW_ID");
                customMap.putAll(custom);
                customResult.clear();
                customResult.add(customMap);
                return getDataTable(customResult);
            } else {
                rspData.setCode(201);
                rspData.setMsg("查询的数据为空");
                return rspData;
            }

        } catch (Exception e) {
            log.error("自定义查询失败!", e);
            rspData.setCode(500);
            rspData.setMsg("查询的sql有误");
            return rspData;
        }
    }

    /**
     * 替换表中的数据
     *
     * @param sql
     * @param customPageReq
     * @return
     */
    public String getSql(String sql, CustomPageReq customPageReq) {

        if (sql.contains("jigou")) {
            if (StringUtils.isEmpty(customPageReq.getDeptId())) {
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                sql = sql.replace("jigou", user.getDeptId());
            } else {
                sql = sql.replace("jigou", customPageReq.getDeptId());
            }
        }
        if (sql.contains("nianfen")) {
            if (StringUtils.isEmpty(customPageReq.getYear())) {
                sql = sql.replace("nianfen", DateUtils.getYear());
            } else {
                sql = sql.replace("nianfen", customPageReq.getYear());
            }
        }
        if (sql.contains("yuefen")) {
            if (StringUtils.isEmpty(customPageReq.getMonths())) {
                sql = sql.replace("yuefen", DateUtils.getMonth());
            } else {
                String[] split = customPageReq.getMonths().split(",");
                StringBuffer monthsSB = new StringBuffer();
                for (String s : split) {
                    monthsSB.append("'" + s + "',");
                }
                String sqlToDo = monthsSB.toString();
                /*去掉最后的, */
                String sqlFinish = sqlToDo.substring(0, sqlToDo.length() - 1);
                sql = sql.replace("'yuefen'", sqlFinish);
            }
        }
        return sql;
    }

}