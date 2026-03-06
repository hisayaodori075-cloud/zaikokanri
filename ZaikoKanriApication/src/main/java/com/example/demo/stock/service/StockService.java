package com.example.demo.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.StockInEntity;
import com.example.demo.stock.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public void save(StockInEntity stock) {
        stockRepository.save(stock);
    }
}
