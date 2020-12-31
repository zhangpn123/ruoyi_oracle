package com.ruoyi.project.report.customquert.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.customquert.dto.CustomPageReq;
import com.ruoyi.project.report.customquert.service.CustomqueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        startPage();
        List<Map<String, Object>> customResult = customqueryService.selectList(customPageReq.getCustomSql());
        return getDataTable(customResult);
    }


    @PostMapping("/getHeaderList")
    @ResponseBody
    public TableDataInfo getHeaderList(@RequestParam("customSql") String customSql) {
        log.info("开始获取查询的表头");
        /*校验数据*/
        TableDataInfo rspData = new TableDataInfo();
        if (StringUtils.isEmpty(customSql)) {
            log.error("要查询的数据为空");
            rspData.setCode(301);
            rspData.setMsg("查询的数据为空");
            return rspData;
        }
        String sql =customSql.trim();

        if (!sql.startsWith("select")) {
            log.error("要查询的数据不是select开头");
            rspData.setCode(301);
            rspData.setMsg("查询的语句不合法：不是select开头的语句");
            return rspData;
        }
        try {
            PageHelper.startPage(1, 1);
            List<Map<String, Object>> customResult = customqueryService.selectList(sql);
            return getDataTable(customResult);
        } catch (Exception e) {
            log.error("自定义查询失败!", e);
            rspData.setCode(500);
            rspData.setMsg("查询的sql有误");
            return rspData;
        }
    }

}
