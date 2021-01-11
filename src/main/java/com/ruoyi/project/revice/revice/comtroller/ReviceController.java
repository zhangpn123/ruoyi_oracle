package com.ruoyi.project.revice.revice.comtroller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: RuoYi
 * @description:
 * @author: authorName
 * @create: 2021-01-11 15:20
 **/
@Controller
@RequestMapping("/review/review")
@Slf4j
public class ReviceController {
    private String prefix = "review";

    @RequiresPermissions("review:review:view")
    @GetMapping()
    public String rivece() {
        return prefix + "/review";
    }
}
