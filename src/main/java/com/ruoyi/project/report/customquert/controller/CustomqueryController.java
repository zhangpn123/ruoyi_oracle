package com.ruoyi.project.report.customquert.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
        StringBuffer sb = new StringBuffer();
        int index = customSql.indexOf("1=1");
        if (index != -1){
            /*将sql分割*/
            String prefix = customSql.substring(0, index + 3);
            String suffix = customSql.substring(index + 3,customSql.length());
            if(StringUtils.isEmpty(customPageReq.getDeptId())){
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                /*拼接条件*/
                customSql = sb.append(prefix).append(" AND co_code like '"+user.getDeptId()+"%' ").append(suffix).toString();
            }else{
                customSql = sb.append(prefix).append(" AND co_code = '"+customPageReq.getDeptId()+"' ").append(suffix).toString();
            }
        }

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
        if(dictData == null || StringUtils.isEmpty(dictData.getDictValue())){
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
            StringBuffer sb = new StringBuffer();
            int index = sql.indexOf("1=1");
            if (index != -1){
                /*将sql分割*/
                String prefix = sql.substring(0, index + 3);
                String suffix = sql.substring(index + 3,sql.length());
                if(StringUtils.isEmpty(customPageReq.getDeptId())){
                    User user = (User) SecurityUtils.getSubject().getPrincipal();
                    /*拼接条件*/
                    sql = sb.append(prefix).append(" AND co_code like '"+user.getDeptId()+"%' ").append(suffix).toString();
                }else{
                    sql = sb.append(prefix).append(" AND co_code = '"+customPageReq.getDeptId()+"' ").append(suffix).toString();
                }
            }
            PageHelper.startPage(1, 1,false);
            List<Map<String, Object>> customResult = customqueryService.selectList(sql);
            if(customResult != null && customResult.size() > 0){
                LinkedHashMap custom = (LinkedHashMap)customResult.get(0);
                custom.remove("ROW_ID");
                Map customMap = new LinkedHashMap();
                customMap.put("ROW_ID","ROW_ID");
                customMap.putAll(custom);
                customResult.clear();
                customResult.add(customMap);
                return getDataTable(customResult);
            }else{
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

}