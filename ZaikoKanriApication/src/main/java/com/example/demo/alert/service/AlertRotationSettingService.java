package com.example.demo.alert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.alert.entity.AlertRotationSettingEntity;
import com.example.demo.alert.repository.AlertRotationSettingRepository;

@Service
public class AlertRotationSettingService {

    @Autowired
    private AlertRotationSettingRepository repository;

    // 設定取得
    public AlertRotationSettingEntity getSetting() {

        return repository.findAll()
                .stream()
                .findFirst()
                .orElse(null);
    }

    // 保存
    public void save(AlertRotationSettingEntity setting) {

        repository.save(setting);
    }

}