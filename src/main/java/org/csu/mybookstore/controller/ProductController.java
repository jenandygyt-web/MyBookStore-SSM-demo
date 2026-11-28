package org.csu.mybookstore.controller;

import org.csu.mybookstore.service.CategoryService;
import org.csu.mybookstore.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 商品页面控制器：负责欢迎页与首页的视图编排与数据装载。
 * 查询规则（过滤、排序、数量限制）全部由 Service 决定，Controller 不写业务规则。
 * 只暴露不带 .html 后缀的地址。
 */
@Controller
public class ProductController {

    private final CategoryService categoryService;

    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /** 欢迎页：品牌介绍 + 登录入口。 */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /** 首页：装载分类导航、新品与精选栏目数据后交给模板渲染。 */
    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("categoryNavigation", categoryService.findNavigation());
        model.addAttribute("newProducts", productService.getNewProducts());
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        return "main";
    }
}