package com.example.smartplant.service;

import com.example.smartplant.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.Map;

@Service
public class UserService {
    private static final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=YOUR_API_KEY";

    public String registerUser(User user) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword());
            FirebaseAuth.getInstance().createUser(request);
            return "회원가입 성공";
        } catch (FirebaseAuthException e) {
            return "회원가입 실패: " + e.getMessage();
        }
    }

    public String loginUserWithCredentials(String email, String password) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map = Map.of(
                "email", email,
                "password", password,
                "returnSecureToken", true
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(FIREBASE_AUTH_URL, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("idToken")) {
                String idToken = (String) responseBody.get("idToken");
                return "로그인 성공. ID 토큰: " + idToken;
            } else {
                return "로그인 실패: 응답에서 ID 토큰을 찾을 수 없습니다.";
            }
        } catch (Exception e) {
            return "로그인 실패: " + e.getMessage();
        }
    }

    // ID 토큰 검증을 통한 로그인 처리 (기존 메서드)
    public String loginUserWithToken(String idToken) {
        try {
            var decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            return "로그인 성공 (UID: " + uid + ")";
        } catch (FirebaseAuthException e) {
            return "로그인 실패: " + e.getMessage();
        }
    }
}