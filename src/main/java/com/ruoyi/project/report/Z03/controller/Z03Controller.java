package com.ruoyi.project.report.Z03.controller;

import com.ruoyi.common.utils.PoiUtil;
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
import com.ruoyi.project.system.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: RuoYi
 * @description: Z03 收入决算表(财决03表)
 * @author: authorName
 * @create: 2020-09-18 10:32
 **/
@Controller
@RequestMapping("/report/Z03")
@Slf4j
public class Z03Controller extends BaseController {

    @Autowired
    private Z03Service z03Service;

    private String prefix = "report/Z03";

    @RequiresPermissions("report:Z03:view")
    @GetMapping()
    public String z03() {
        return prefix + "/Z03";
    }

    @RequiresPermissions("report:Z03:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Z03Report z03Report) {
        log.info("开始查询Z03报表数据");
       List<String> fieldList = new ArrayList<>();
        fieldList.add("6001");
        fieldList.add("6201");
        fieldList.add("6101");
        fieldList.add("6410");
        fieldList.add("fees");
        fieldList.add("6310");
        fieldList.add("6601");
        fieldList.add("6609");
        List<Map<String, String>> replaceMap = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("src", "6609");//需要合并的数据
        map.put("dest", "6601");//将要合并到的数据
        replaceMap.add(map);
        List<Map<String,Object>> list = z03Service.selectRoleList(fieldList,replaceMap);
        return getDataTable(list);
    }

    /**
     * 报表导出
     *
     */
    @Log(title = "报表统计", businessType = BusinessType.EXPORT)
    @RequiresPermissions("report:Z03:export")
    @RequestMapping("/export")
    @ResponseBody
    public AjaxResult exportReport(Z03Report z03Report) throws IOException {
            log.info("Start generating reports");
            //构建Excel
            String fileName = System.currentTimeMillis() + ".xls";
            String excelPath = RuoYiConfig.getDownloadPath()+ fileName;


            //构建excel数据
            List<String> fieldList = new ArrayList<>();
            fieldList.add("6001");
            fieldList.add("6201");
            fieldList.add("6101");
            fieldList.add("6410");
            fieldList.add("fees");
            fieldList.add("6310");
            fieldList.add("6601");
            fieldList.add("6609");
            List<Map<String, String>> replaceMap = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("src", "6609");//需要合并的数据
            map.put("dest", "6601");//将要合并到的数据
            replaceMap.add(map);

            List<Map<String, Object>> resultList = z03Service.selectRoleList(fieldList, replaceMap);
            /*重新组合数据*/
            List<Map<String, Object>> data = new LinkedList<>();
            Map totalMap = new HashMap();
            totalMap.put("bAccName", "合计");
            data.add(0, totalMap);
            BigDecimal total = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal one = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal two = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal three = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal four = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal five = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal six = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal seven = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);

            int index = 1;
            for (Map<String, Object> datum : resultList) {
                total = total.add(new BigDecimal(datum.get("total").toString()));
                one = one.add(new BigDecimal(datum.get("6001").toString()));
                two = two.add(new BigDecimal(datum.get("6201").toString()));
                three = three.add(new BigDecimal(datum.get("6101").toString()));
                four = four.add(new BigDecimal(datum.get("fees").toString()));
                five = five.add(new BigDecimal(datum.get("6410").toString()));
                six = six.add(new BigDecimal(datum.get("6310").toString()));
                seven = seven.add(new BigDecimal(datum.get("6601").toString()));
                data.add(index, datum);
                index++;
            }
            totalMap.put("bAccName", "合计");
            totalMap.put("total", total);
            totalMap.put("6001", one);
            totalMap.put("6201", two);
            totalMap.put("6101", three);
            totalMap.put("fees", four);
            totalMap.put("6410", five);
            totalMap.put("6310", six);
            totalMap.put("6601", seven);
            data.remove(0);
            data.add(0, totalMap);

            //构建excel数据
            String[] headNames = {"类", "款", "项", "科目名称", "本年收入合计", "财政拨款收入", "上级补助收入", "小计", "其中：教育收费", "经营收入", "附属单位上缴收入", "其他收入"};
            String[] keys = {"bAccCode", "bAccCode", "bAccCode", "bAccName", "total", "6001", "6201", "6101", "fees", "6410", "6310", "6601"};


            int colWidths[] = {200, 200, 200, 300, 400, 400, 400, 400, 600, 400, 500, 400};
            InputStream input = PoiUtil.getExcelFile(data, "Z03 收入决算表(财决03表)", 13, keys, colWidths);
            ExcelUtil.writeExcel(input, excelPath);
            return AjaxResult.success(fileName);

    }



}
