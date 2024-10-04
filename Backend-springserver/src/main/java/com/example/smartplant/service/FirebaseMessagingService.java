package com.example.smartplant.service;

import com.example.smartplant.model.SensorData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService { // Firebase 메시징 기능

    // 토양 수분 부족 시 알림을 전송하는 기능
    public void sendSoilMoistureAlert(SensorData sensorData) {
        String messageBody = "토양 수분이 " + sensorData.getSoilMoisture() + "% 로 너무 부족합니다.";

        // Firebase Admin SDK를 사용하여 "alerts"라는 토픽에 구독된 모든 사용자에게 알림을 전송하는 코드
        Message message = Message.builder()
                .putData("title", "토양 수분 부족 알림")
                .putData("body", messageBody)
                .setTopic("alerts")
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }

}
