package com.example.smartplant.controller;

import com.example.smartplant.model.SensorData;
import com.example.smartplant.service.FirebaseMessagingService;
import com.example.smartplant.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/sensors")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    // 모든 센서 데이터 조회
    @GetMapping
    public CompletableFuture<List<SensorData>> getSensorData() {
        return sensorDataService.getAllSensorData();
    }

    // 센서 데이터 추가
    @PostMapping
    public CompletableFuture<Void> addSensorData(@RequestBody SensorData sensorData) {
        sensorData.setTimestamp(System.currentTimeMillis());
        CompletableFuture<Void> result = sensorDataService.saveSensorData(sensorData);

        // 수분 부족 시 Firebase 메시지 전송
        if (sensorData.getSoilMoisture() < 30) {
            firebaseMessagingService.sendSoilMoistureAlert(sensorData);
        }

        return result;
    }

    // 센서 데이터 업데이트
    @PutMapping("/{id}")
    public CompletableFuture<Void> updateSensorData(@PathVariable String id, @RequestBody SensorData sensorData) {
        sensorData.setId(id);
        sensorData.setTimestamp(System.currentTimeMillis());
        return sensorDataService.updateSensorData(sensorData);
    }

    // 센서 데이터 삭제
    @DeleteMapping("/{id}")
    public CompletableFuture<Void> deleteSensorData(@PathVariable String id) {
        return sensorDataService.deleteSensorData(id);
    }

    // Bluetooth로 센서 데이터 수신
    @PostMapping("/bluetooth")
    public CompletableFuture<Void> receiveBluetoothData(@RequestBody SensorData sensorData) {
        sensorData.setTimestamp(System.currentTimeMillis());
        return sensorDataService.saveSensorData(sensorData);
    }
}
