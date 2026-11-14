package org.csu.mybookstore.controller;

import org.csu.mybookstore.dto.UserReadResult;
import org.csu.mybookstore.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

}
