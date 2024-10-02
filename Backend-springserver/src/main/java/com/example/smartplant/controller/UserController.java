package com.example.smartplant.controller;

import com.example.smartplant.model.User;
import com.example.smartplant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // API 엔드포인트
@CrossOrigin(origins = "*") // 모든 origin 허용
public class UserController { // 새로운 사용자 관리 API

    @Autowired
    private UserService userService;

    // 회원가입 API
    @PostMapping("/register")  // API 엔드포인트
    public String registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // 로그인 API
    @PostMapping("/login")  // API 엔드포인트
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        try {
            String token = userService.loginUserWithCredentials(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(token); // 성공 시 토큰 반환
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Email")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 이메일 형식입니다.");
            } else if (e.getMessage().contains("Password")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 잘못되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 정보가 올바르지 않습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
}
