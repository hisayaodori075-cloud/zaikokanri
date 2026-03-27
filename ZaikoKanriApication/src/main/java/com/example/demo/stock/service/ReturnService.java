package com.example.demo.stock.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.ReturnEntity;
import com.example.demo.stock.repository.ReturnRepository;

@Service
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    // 返品登録
    public void save(ReturnEntity returnEntity) {

        returnEntity.setReturnDate(LocalDate.now());
        returnEntity.setDeleteFlag(0);

        returnRepository.save(returnEntity);
    }

    // 返品取消（論理削除）
    public void delete(Integer id) {

        ReturnEntity entity = returnRepository.findById(id).orElse(null);

        if (entity != null && entity.getDeleteFlag() == 0) {
            entity.setDeleteFlag(1);
            returnRepository.save(entity);
        }
    }

    // 返品合計取得（在庫計算用）
    public Integer getTotalReturnByProductId(Integer productId) {
        return returnRepository.sumReturnQuantityByProductId(productId);
    }
}
