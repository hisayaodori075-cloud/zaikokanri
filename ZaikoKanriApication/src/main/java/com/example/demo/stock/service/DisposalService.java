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

    // すべて取得（論理削除されていないものだけ）
    public List<DisposalEntity> findAll() {
        return disposalRepository.findByDeletedFalse();
    }

    // IDで取得（nullの場合はOptional.empty、論理削除されていないものだけ）
    public Optional<DisposalEntity> findOptionalById(Integer id) {
        return disposalRepository.findByIdAndDeletedFalse(id);
    }

    // IDで取得（null許容版、従来のメソッド、論理削除対応）
    public DisposalEntity findById(Integer id) {
        return disposalRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    // 保存（新規 or 更新）
    public DisposalEntity save(DisposalEntity disposal) {
        return disposalRepository.save(disposal);
    }

    // 論理削除
    public void delete(Integer id) {
        DisposalEntity disposal = findById(id);
        if (disposal != null) {
            disposal.setDeleted(true); // 論理削除フラグ
            disposalRepository.save(disposal);
        }
    }

    // 複数ID検索（論理削除されていないものだけ）
    public List<DisposalEntity> findByIds(List<Integer> ids) {
        return disposalRepository.findByIdInAndDeletedFalse(ids);
    }
}