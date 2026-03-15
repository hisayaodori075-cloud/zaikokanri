package com.example.demo.alert.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.alert.entity.AlertSettingEntity;
import com.example.demo.alert.repository.AlertSettingRepository;

@Service
public class AlertService {

    @Autowired
    private AlertSettingRepository alertSettingRepository;

    /**
     * アラート設定を保存（既存があれば更新）
     */
    public void save(AlertSettingEntity alertSetting) {
        // productId で既存を取得
        AlertSettingEntity exist = alertSettingRepository.findByProductId(alertSetting.getProductId());
        if (exist != null) {
            // 更新
            alertSetting.setId(exist.getId());
        }
        alertSettingRepository.save(alertSetting);
    }

    /**
     * 全アラート設定を List で取得
     */
    public List<AlertSettingEntity> findAll() {
        return alertSettingRepository.findAll();
    }

    /**
     * 全アラート設定を Map に変換（productId → AlertSettingEntity）
     */
    public Map<Integer, AlertSettingEntity> findAllMap() {
        return findAll().stream()
                        .collect(Collectors.toMap(AlertSettingEntity::getProductId, a -> a));
    }

    /**
     * 商品IDで1件取得
     */
    public AlertSettingEntity findByProductId(Integer productId) {
        return alertSettingRepository.findByProductId(productId);
    }
}