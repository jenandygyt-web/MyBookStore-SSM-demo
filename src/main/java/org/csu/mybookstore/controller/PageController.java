package org.csu.mybookstore.controller;

import org.csu.mybookstore.demo.DemoBook;
import org.csu.mybookstore.demo.DemoCartLine;
import org.csu.mybookstore.demo.DemoCatalog;
import org.csu.mybookstore.demo.DemoCategory;
import org.csu.mybookstore.demo.DemoOrder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面控制器：负责把演示数据装进 Model，再交给 Thymeleaf 渲染。
 * 只做视图编排，不写业务规则；数据暂由 DemoCatalog 提供，后续换成 Service 即可。
 */
@Controller
public class PageController {

    private final DemoCatalog catalog;

    public PageController(DemoCatalog catalog) {
        this.catalog = catalog;
    }

    @ModelAttribute("year")
    public int year() {
        return java.time.Year.now().getValue();
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return catalog.cartCount();
    }

    @ModelAttribute("categories")
    public List<DemoCategory> categories() {
        return catalog.categories();
    }

    /** 欢迎页：品牌介绍 + 登录入口。 */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("picks", catalog.editorsPicks());
        return "index";
    }

    /** 首页。 */
    @GetMapping({"/home", "/Main.html"})
    public String home(Model model) {
        model.addAttribute("featured", catalog.first("BK-008"));
        model.addAttribute("newBooks", catalog.newArrivals());
        model.addAttribute("picks", catalog.editorsPicks());
        model.addAttribute("ranking", catalog.bestsellers());
        return "main";
    }

    /** 图书列表：支持分类、关键词、价格区间与排序。 */
    @GetMapping({"/category", "/Category.html"})
    public String category(
            @RequestParam(value = "c", required = false) String c,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "p", required = false) String p,
            @RequestParam(value = "sort", defaultValue = "default") String sort,
            Model model) {

        List<DemoBook> list = new ArrayList<>(catalog.books());

        if (c != null && !c.isBlank() && !"all".equals(c)) {
            list.removeIf(b -> !b.getCategoryCode().equals(c));
        }
        if (q != null && !q.isBlank()) {
            String keyword = q.trim();
            list.removeIf(b -> !contains(b.getTitle(), keyword)
                    && !contains(b.getAuthor(), keyword)
                    && !contains(b.getPublisher(), keyword));
        }
        if (p != null && !p.isBlank()) {
            switch (p) {
                case "under50" -> list.removeIf(b -> b.getPrice().compareTo(BigDecimal.valueOf(50)) >= 0);
                case "50to100" -> list.removeIf(b -> b.getPrice().compareTo(BigDecimal.valueOf(50)) < 0
                        || b.getPrice().compareTo(BigDecimal.valueOf(100)) > 0);
                case "over100" -> list.removeIf(b -> b.getPrice().compareTo(BigDecimal.valueOf(100)) <= 0);
                default -> { }
            }
        }
        switch (sort) {
            case "price-asc" -> list.sort(Comparator.comparing(DemoBook::getPrice));
            case "price-desc" -> list.sort(Comparator.comparing(DemoBook::getPrice).reversed());
            case "rating" -> list.sort(Comparator.comparing(DemoBook::getRating).reversed());
            case "sales" -> list.sort(Comparator.comparing(DemoBook::getSales).reversed());
            default -> { }
        }

        DemoCategory activeCategory = catalog.categories().stream()
                .filter(cat -> cat.getCode().equals(c))
                .findFirst()
                .orElse(null);

        Map<String, Long> counts = new HashMap<>();
        counts.put("all", (long) catalog.books().size());
        catalog.categories().forEach(cat -> counts.put(cat.getCode(), catalog.countOf(cat.getCode())));

        model.addAttribute("categoryCounts", counts);
        model.addAttribute("books", list);
        model.addAttribute("total", list.size());
        model.addAttribute("activeCategory", activeCategory);
        model.addAttribute("keyword", q == null ? "" : q);
        model.addAttribute("price", p == null ? "" : p);
        model.addAttribute("sort", sort);
        return "category";
    }

    /** 图书详情。 */
    @GetMapping({"/product/{sku}", "/Product.html"})
    public String product(@PathVariable(value = "sku", required = false) String sku, Model model) {
        if (sku == null || sku.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "缺少图书编号");
        }
        DemoBook book = catalog.find(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "没有找到这本书：" + sku));
        model.addAttribute("book", book);
        model.addAttribute("related", catalog.related(book, 4));
        return "product";
    }

    /** 购物车。 */
    @GetMapping({"/cart", "/Cart.html"})
    public String cart(Model model) {
        List<DemoCartLine> lines = catalog.cartLines();
        BigDecimal subtotal = catalog.cartSubtotal();
        BigDecimal shipping = catalog.shippingOf(subtotal);
        model.addAttribute("lines", lines);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", subtotal.add(shipping));
        model.addAttribute("itemCount", catalog.cartCount());
        return "cart";
    }

    /** 登录 / 注册。 */
    @GetMapping({"/login", "/SignonForm.html"})
    public String login(Model model) {
        model.addAttribute("picks", catalog.editorsPicks());
        return "login";
    }

    /** 演示环境没有认证逻辑：提交后直接回到首页。 */
    @PostMapping("/login")
    public String doLogin() {
        return "redirect:/home";
    }

    @PostMapping("/register")
    public String doRegister() {
        return "redirect:/home";
    }

    /** 会员中心：个人资料。 */
    @GetMapping({"/profile", "/EditProfile.html"})
    public String profile(Model model) {
        model.addAttribute("demoUser", "陈默");
        model.addAttribute("demoUsername", "chenmo");
        model.addAttribute("demoEmail", "chenmo@example.com");
        model.addAttribute("demoPhone", "138 0013 6621");
        model.addAttribute("demoAddress", "上海市杨浦区邯郸路 220 号 3 号楼 502 室");
        return "profile";
    }

    @PostMapping("/profile")
    public String saveProfile() {
        return "redirect:/profile";
    }

    /** 修改密码。 */
    @GetMapping({"/password", "/ChangePassword.html"})
    public String password() {
        return "password";
    }

    @PostMapping("/password")
    public String savePassword() {
        return "redirect:/profile";
    }

    /** 订单列表。 */
    @GetMapping({"/orders", "/ListOrders.html"})
    public String orders(Model model) {
        model.addAttribute("orders", catalog.orders());
        return "orders";
    }

    /** 订单详情。 */
    @GetMapping({"/orders/{id}", "/ViewOrder.html"})
    public String order(@PathVariable(value = "id", required = false) Long id, Model model) {
        if (id == null) {
            return "redirect:/orders";
        }
        DemoOrder order = catalog.findOrder(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "没有找到该订单"));
        model.addAttribute("order", order);
        return "order";
    }

    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword.toLowerCase());
    }
}
