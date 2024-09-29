package com.example.smartplant.controller;

import com.example.smartplant.model.SensorData;
import com.example.smartplant.service.FirebaseMessagingService;
import com.example.smartplant.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/sensors")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // 웹소켓 메시징 템플릿

    // 모든 센서 데이터 조회
    @GetMapping
    public CompletableFuture<List<SensorData>> getSensorData() {
        return sensorDataService.getAllSensorData();
    }

    // 특정 시간 범위의 센서 데이터 조회
    @GetMapping("/range")
    public CompletableFuture<List<SensorData>> getSensorDataInRange(
            @RequestParam("start") long startTimestamp,
            @RequestParam("end") long endTimestamp
    ) {
        return sensorDataService.getSensorDataInRange(startTimestamp, endTimestamp);
    }

    // 센서 데이터 추가 및 실시간 업데이트
    @PostMapping
    public CompletableFuture<Void> addSensorData(@RequestBody SensorData sensorData) {
        sensorData.setTimestamp(System.currentTimeMillis());
        CompletableFuture<Void> result = sensorDataService.saveSensorData(sensorData);

        // 수분 부족 시 Firebase 메시지 전송
        if (sensorData.getSoilMoisture() < 30) {
            firebaseMessagingService.sendSoilMoistureAlert(sensorData);
        }

        // 온도 임계값 초과 시 Firebase 메시지 전송
        if (sensorData.getTemperature() > 30) { // 임계값은 필요에 따라 조정
            firebaseMessagingService.sendTemperatureAlert(sensorData);
        }

        // 모든 클라이언트에게 최신 데이터 전송
        messagingTemplate.convertAndSend("/topic/sensorData", sensorData);

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

    // 데이터 분석 (일별, 주별, 월별)
    @GetMapping("/analysis")
    public ResponseEntity<Map<String, List<Double>>> getSensorDataAnalysis(
            @RequestParam("period") String period
    ) throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = now;
        switch (period) {
            case "daily":
                start = now.minusDays(1);
                break;
            case "weekly":
                start = now.minusWeeks(1);
                break;
            case "monthly":
                start = now.minusMonths(1);
                break;
        }

        long startTimestamp = start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTimestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        CompletableFuture<List<SensorData>> dataFuture = sensorDataService.getSensorDataInRange(startTimestamp, endTimestamp);
        List<SensorData> sensorDataList = dataFuture.get();

        // 데이터 분석 및 결과 생성
        Map<String, List<Double>> analysisResult = analyzeSensorData(sensorDataList, period, start, now);

        return ResponseEntity.ok(analysisResult);
    }

    private Map<String, List<Double>> analyzeSensorData(List<SensorData> sensorDataList, String period, LocalDateTime start, LocalDateTime end) {
        Map<String, List<Double>> result = new HashMap<>();
        List<Double> temperatureData = new ArrayList<>();
        List<Double> humidityData = new ArrayList<>();
        List<Double> soilMoistureData = new ArrayList<>();
        result.put("temperature", temperatureData);
        result.put("humidity", humidityData);
        result.put("soilMoisture", soilMoistureData);

        long timeUnitMillis;
        switch (period) {
            case "daily":
                timeUnitMillis = ChronoUnit.HOURS.getDuration().toMillis();
                break;
            case "weekly":
                timeUnitMillis = ChronoUnit.DAYS.getDuration().toMillis();
                break;
            case "monthly":
                timeUnitMillis = ChronoUnit.DAYS.getDuration().toMillis() * 7; // 주별 평균으로 계산
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        LocalDateTime current = start;
        while (current.isBefore(end)) {
            LocalDateTime next = current.plus(timeUnitMillis, ChronoUnit.MILLIS);
            double temperatureSum = 0, humiditySum = 0, soilMoistureSum = 0;
            int count = 0;
            for (SensorData data : sensorDataList) {
                LocalDateTime dataTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(data.getTimestamp()), ZoneId.systemDefault());
                if (dataTime.isAfter(current) && dataTime.isBefore(next)) {
                    temperatureSum += data.getTemperature();
                    humiditySum += data.getHumidity();
                    soilMoistureSum += data.getSoilMoisture();
                    count++;
                }
            }
            if (count > 0) {
                temperatureData.add(temperatureSum / count);
                humidityData.add(humiditySum / count);
                soilMoistureData.add(soilMoistureSum / count);
            } else {
                temperatureData.add(0.0);
                humidityData.add(0.0);
                soilMoistureData.add(0.0);
            }
            current = next;
        }

        return result;
    }

    // 자동화 규칙 설정
    private Map<String, Double> automationRules = new HashMap<>();

    @MessageMapping("/automation/rules") // STOMP 메시지 처리
    @SendTo("/topic/automation/status") // 결과를 사용자에게 전송
    public Map<String, Double> setAutomationRules(Map<String, Double> rules) {
        automationRules.putAll(rules);
        return automationRules;
    }

    // 자동화 규칙을 기반으로 조치를 취하는 함수 (센서 데이터 추가 시 호출)
    private void checkAutomationRules(SensorData data) {
        if (automationRules.containsKey("humidityThreshold") && data.getHumidity() < automationRules.get("humidityThreshold")) {

            System.out.println("토양 수분이 임계값 이하로 떨어졌습니다. 물을 주어야 합니다.");
            // 여기서 필요한 작업 수행 (예: IoT 장치 제어, 외부 서비스 호출 등)
        }

        if (automationRules.containsKey("temperatureThreshold") && data.getTemperature() > automationRules.get("temperatureThreshold")) {

            System.out.println("온도가 임계값 이상으로 올라갔습니다. 환풍기를 켜야 합니다.");
            // 여기서 필요한 작업 수행 (예: IoT 장치 제어, 외부 서비스 호출 등)
        }
    }
}