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

        Message message = Message.builder()
                .putData("title", "토양 수분 부족 알림")
                .putData("body", messageBody)
                .setTopic("alerts")
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }

    // 온도 이상 시 알림을 전송하는 기능 추가
    public void sendTemperatureAlert(SensorData sensorData) {
        String messageBody = "온도가 " + sensorData.getTemperature() + "°C 로 너무 높습니다.";

        Message message = Message.builder()
                .putData("title", "온도 이상 알림")
                .putData("body", messageBody)
                .setTopic("alerts") // 동일한 토픽 사용 또는 필요에 따라 다른 토픽 설정 가능
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }
}
