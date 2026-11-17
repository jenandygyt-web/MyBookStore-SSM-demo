package org.csu.mybookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 视图控制器：把 HTML 原型作为 Thymeleaf 模板渲染。
 * 模板位于 src/main/resources/templates，静态资源位于 src/main/resources/static。
 * 页面之间的相对链接（css/、js/、images/、*.html）在根路径下可直接复用。
 */
@Controller
public class PageController {

    /** 所有页面共享的模型属性，模板里直接用 \${year} 取用。 */
    @ModelAttribute("year")
    public int year() {
        return java.time.Year.now().getValue();
    }

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping("/Main.html")
    public String main(Model model) {
        // 演示用数据，后续接入购物车服务后替换
        model.addAttribute("cartCount", 1);
        return "Main";
    }

    @GetMapping("/Category.html")
    public String category() {
        return "Category";
    }

    @GetMapping("/Product.html")
    public String product() {
        return "Product";
    }

    @GetMapping("/Cart.html")
    public String cart() {
        return "Cart";
    }

    @GetMapping("/SignonForm.html")
    public String signonForm() {
        return "SignonForm";
    }

    @GetMapping("/ChangePassword.html")
    public String changePassword() {
        return "ChangePassword";
    }

    @GetMapping("/EditProfile.html")
    public String editProfile() {
        return "EditProfile";
    }

    @GetMapping("/ListOrders.html")
    public String listOrders() {
        return "ListOrders";
    }

    @GetMapping("/ViewOrder.html")
    public String viewOrder() {
        return "ViewOrder";
    }
}
