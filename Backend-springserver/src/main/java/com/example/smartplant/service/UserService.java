package com.example.smartplant.service;

import com.example.smartplant.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService { // 새로운 사용자 서비스

    private Map<String, User> users = new HashMap<>();

    public void registerUser(User user) {
        users.put(user.getEmail(), user);
    }

    public String loginUser(User user) {
        User storedUser = users.get(user.getEmail());
        if (storedUser != null && storedUser.getPassword().equals(user.getPassword())) {
            return "로그인 성공";
        } else {
            return "잘못된 로그인 정보";
        }
    }
}
