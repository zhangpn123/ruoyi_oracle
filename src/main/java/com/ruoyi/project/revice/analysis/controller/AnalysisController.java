package com.ruoyi.project.revice.analysis.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-01-11 15:21
 **/
@Controller
@RequestMapping("/review/analysis")
@Slf4j
public class AnalysisController {
    private String prefix = "review";

    @RequiresPermissions("review:analysis:view")
    @GetMapping()
    public String analysis() {
        return prefix + "/analysis";
    }
}
