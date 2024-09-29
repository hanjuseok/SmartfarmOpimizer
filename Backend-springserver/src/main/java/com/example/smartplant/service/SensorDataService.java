package com.example.smartplant.service;

import com.example.smartplant.model.SensorData;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service // 데이터의 CRUD 작업 수행가능
public class SensorDataService { // Firebase와 연동하여 센서 데이터를 처리하는 서비스

    private final DatabaseReference databaseReference;

    public SensorDataService(FirebaseApp firebaseApp) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
        databaseReference = database.getReference("sensorData");
    }

    // 데이터 저장(
    public CompletableFuture<Void> saveSensorData(SensorData sensorData) {
        String key = databaseReference.push().getKey();
        if (key != null) {
            sensorData.setId(key);
            ApiFuture<Void> apiFuture = databaseReference.child(key).setValueAsync(sensorData);
            return toCompletableFuture(apiFuture);
        }
        return CompletableFuture.completedFuture(null);
    }

    // 데이터 조회
    public CompletableFuture<List<SensorData>> getAllSensorData() {
        CompletableFuture<List<SensorData>> future = new CompletableFuture<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<SensorData> sensorDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SensorData sensorData = snapshot.getValue(SensorData.class);
                    sensorDataList.add(sensorData);
                }
                future.complete(sensorDataList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    // 데이터 업데이트
    public CompletableFuture<Void> updateSensorData(SensorData sensorData) {
        if (sensorData.getId() != null) {
            ApiFuture<Void> apiFuture = databaseReference.child(sensorData.getId()).setValueAsync(sensorData);
            return toCompletableFuture(apiFuture);
        }
        return CompletableFuture.completedFuture(null);
    }

    // 데이터 삭제
    public CompletableFuture<Void> deleteSensorData(String id) {
        ApiFuture<Void> apiFuture = databaseReference.child(id).removeValueAsync();
        return toCompletableFuture(apiFuture);
    }

    // ApiFuture를 CompletableFuture로 변환
    private <T> CompletableFuture<T> toCompletableFuture(ApiFuture<T> apiFuture) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        apiFuture.addListener(() -> {
            try {
                completableFuture.complete(apiFuture.get());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        }, Runnable::run);
        return completableFuture;
    }

    // 특정 시간 범위의 데이터 조회
    public CompletableFuture<List<SensorData>> getSensorDataInRange(long startTimestamp, long endTimestamp) {
        CompletableFuture<List<SensorData>> future = new CompletableFuture<>();
        databaseReference.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<SensorData> sensorDataList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SensorData sensorData = snapshot.getValue(SensorData.class);
                            sensorDataList.add(sensorData);
                        }
                        future.complete(sensorDataList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        future.completeExceptionally(databaseError.toException());
                    }
                });
        return future;
    }

}
