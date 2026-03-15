package com.example.demo.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.alert.entity.AlertRotationSettingEntity;

public interface AlertRotationSettingRepository 
        extends JpaRepository<AlertRotationSettingEntity, Integer> {

}