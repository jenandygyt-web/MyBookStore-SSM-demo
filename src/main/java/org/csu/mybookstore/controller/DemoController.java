package org.csu.mybookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

    @GetMapping(value = "/demo",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String demo(){
        return "Spring MVC 初始化成功！hahaha";
    }
}
