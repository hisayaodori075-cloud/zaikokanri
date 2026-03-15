package com.example.demo.alert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.alert.entity.AlertSettingEntity;

@Repository
public interface AlertSettingRepository
        extends JpaRepository<AlertSettingEntity, Integer> {

    // 指定された productId のアラート設定を取得
    AlertSettingEntity findByProductId(Integer productId);
    
    List<AlertSettingEntity> findAllByProductId(Integer productId);

}