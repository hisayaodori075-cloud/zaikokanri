package com.example.demo.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.StockTakeEntity;
import com.example.demo.stock.repository.StockTakeRepository;

@Service
public class StockTakeService {

    @Autowired
    private StockTakeRepository repository;

    public void save(StockTakeEntity entity) {
        repository.save(entity);
    }

    public StockTakeEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<StockTakeEntity> findAllNotDeleted() {
        return repository.findByDeletedFalse();
    }
}