package org.csu.mybookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 商品页面控制器：负责欢迎页与首页的视图跳转，暂不接入数据库数据。
 * 只暴露不带 .html 后缀的地址。
 */
@Controller
public class ProductController {

    /** 欢迎页：品牌介绍 + 登录入口。 */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /** 首页。 */
    @GetMapping("/main")
    public String main() {
        return "main";
    }
}