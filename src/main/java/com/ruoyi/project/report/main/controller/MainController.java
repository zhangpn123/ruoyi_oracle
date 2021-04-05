package com.ruoyi.project.report.main.controller;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.Map2Bean;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.page.BaseResp;
import com.ruoyi.framework.web.page.Response;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.report.Z03.service.Z03Service;
import com.ruoyi.project.report.dto.ReportCondition;
import com.ruoyi.project.report.dto.ResponseMain;
import com.ruoyi.project.report.main.service.MainService;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.dept.service.IDeptService;
import com.ruoyi.project.system.dict.domain.DictData;
import com.ruoyi.project.system.dict.service.IDictTypeService;
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private MainService mainService;

    // @RequiresPermissions("report:main:list")
    @PostMapping("/list")
    @ResponseBody
    public BaseResp<List<ResponseMain>> list(ReportCondition reportCondition) {
        log.info("开始查询首页报表数据");
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

        List<ResponseMain> list = mainService.selectData(fieldList,replaceMap,reportCondition);
        for (ResponseMain responseMain : list) {
            for (DictData dictDatum : dictData) {
                if (responseMain.getCode().equals(dictDatum.getDictValue())){
                    responseMain.setName(dictDatum.getDictLabel());
                }
            }
        }
        return Response.ok(list);
    }


    @PostMapping("/detail")
    @ResponseBody
    public TableDataInfo detail(ReportCondition reportCondition) {
        log.info("开始查询首页报表明细数据");

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
            List<DictData> dictData = dictTypeService.selectDictDataByType("report_main_column");

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
            for (ResponseMain responseMain : responseMains) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("coName",reportCondition.getDeptName());
                map1.put("crAmt",responseMain.getValue());
                map1.put("accName",responseMain.getName());
                list.add(map1);
            }
        }else{
            fieldList.add(reportCondition.getAccCode());
             list = mainService.selectDataDetail(fieldList, replaceMap, reportCondition);
        }
        return getDataTable(list);
    }

}
