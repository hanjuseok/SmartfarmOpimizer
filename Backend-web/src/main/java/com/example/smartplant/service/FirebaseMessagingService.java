package com.example.smartplant.service;

import com.example.smartplant.model.SensorData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService { // Firebase 메시징 기능

    // 토양 수분 부족 시 알림을 전송하는 기능
    public void sendSoilMoistureAlert(SensorData sensorData) {
        String messageBody = "Soil moisture level is too low: " + sensorData.getSoilMoisture() + "%";

        Message message = Message.builder()
                .putData("title", "Soil Moisture Alert")
                .putData("body", messageBody)
                .setTopic("alerts")
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }
}
