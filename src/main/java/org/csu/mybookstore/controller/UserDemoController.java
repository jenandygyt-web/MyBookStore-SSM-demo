package org.csu.mybookstore.controller;

import org.csu.mybookstore.dto.UserReadResult;
import org.csu.mybookstore.service.UserService;
import org.springframework.context.annotation.Profile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

/**
 * 用户查询演示接口：仅在 dev 环境注册，演示通过 Service 读取展示字段并按状态码返回。
 * 不返回实体、不直接调用 Mapper、不把异常一律转成 404。
 */
@Controller
@Profile("dev")
public class UserDemoController {

    private final UserService userService;

    public UserDemoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/demo/user")
    @ResponseBody
    public ResponseEntity<UserReadResult> user(
            @RequestParam(name = "username", defaultValue = "alice") String username) {
        Optional<UserReadResult> result = userService.findByUsername(username);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 用户信息页面：复用同一个 UserService 查询，把结果交给 Thymeleaf 渲染。
     * 不加 @ResponseBody —— 返回的字符串会被解析为视图名（demo/user），而不是响应体内容。
     * 查不到时直接置 404 状态码，不额外写查询逻辑。
     */
    @GetMapping("/demo/user/page")
    public String userPage(
            @RequestParam(name = "username", defaultValue = "alice") String username,
            Model model,
            HttpServletResponse response) {
        model.addAttribute("username", username);
        Optional<UserReadResult> result = userService.findByUsername(username);
        if (result.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("message", "未找到用户：" + username);
            return "demo/user";
        }
        model.addAttribute("message", "Thymeleaf 初始化成功！");
        model.addAttribute("user", result.get());
        return "demo/user";
    }

}
