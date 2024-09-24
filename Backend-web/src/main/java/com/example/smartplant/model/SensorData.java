package com.example.smartplant.model;

public class SensorData { // 센서 데이터를 저장하기 위한 모델 클래스
    private String id;
    private double temperature;
    private double humidity;
    private double soilMoisture;
    private long timestamp;

    // 기본 생성자
    public SensorData() {}

    // 생성자
    public SensorData(String id, double temperature, double humidity, double soilMoisture, long timestamp) {
        this.id = id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.soilMoisture = soilMoisture;
        this.timestamp = timestamp;
    }

    // Getters and Setters 를 통해서 데이터를 저장하고 읽어옵니다.
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(double soilMoisture) { this.soilMoisture = soilMoisture; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
// 각 센서 데이터의 id, temperature(온도), humidity(습도), soilMoisture(토양 수분), timestamp(시간)를 정의