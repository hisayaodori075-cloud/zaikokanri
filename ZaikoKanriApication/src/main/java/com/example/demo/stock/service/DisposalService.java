package com.example.demo.stock.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.DisposalEntity;
import com.example.demo.stock.repository.DisposalRepository;

@Service
public class DisposalService {

    @Autowired
    private DisposalRepository disposalRepository;

    // すべて取得
    public List<DisposalEntity> findAll() {
        return disposalRepository.findAll();
    }

    // IDで取得
    public DisposalEntity findById(Integer id) {
        Optional<DisposalEntity> optional = disposalRepository.findById(id);
        return optional.orElse(null);
    }

    // 保存（新規 or 更新）
    public DisposalEntity save(DisposalEntity disposal) {
        return disposalRepository.save(disposal);
    }

    // 削除（論理削除にしたい場合はここでフラグ更新）
    public void delete(Integer id) {
        DisposalEntity disposal = findById(id);
        if (disposal != null) {
            disposal.setDeleted(true); // 論理削除フラグを使う場合
            disposalRepository.save(disposal);
        }
    }
}