package com.example.smartplant.controller;

import com.example.smartplant.model.User;
import com.example.smartplant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController { // 새로운 사용자 관리 API

    @Autowired
    private UserService userService;

    // 회원가입 API
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // 로그인 API
    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        return userService.loginUserWithCredentials(user.getEmail(), user.getPassword());
    }
}
