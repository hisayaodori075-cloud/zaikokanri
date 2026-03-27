package com.example.demo.stock.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.ReturnEntity;
import com.example.demo.stock.repository.ReturnRepository;

@Service
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    // すべて取得（論理削除されていないものだけ）
    public List<ReturnEntity> findAll() {
        return returnRepository.findByDeletedFalse();
    }

    // IDで取得（nullの場合はOptional.empty、論理削除されていないものだけ）
    public Optional<ReturnEntity> findOptionalById(Integer id) {
        return returnRepository.findByIdAndDeletedFalse(id);
    }

    // IDで取得（null許容版、従来のメソッド、論理削除対応）
    public ReturnEntity findById(Integer id) {
        return returnRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    // 保存（新規 or 更新）
    public ReturnEntity save(ReturnEntity returnData) {
        return returnRepository.save(returnData);
    }

    // 論理削除
    public void delete(Integer id) {
        ReturnEntity returnData = findById(id);
        if (returnData != null) {
            returnData.setDeleted(true); // 論理削除フラグ
            returnRepository.save(returnData);
        }
    }

    // 複数ID検索（論理削除されていないものだけ）
    public List<ReturnEntity> findByIds(List<Integer> ids) {
        return returnRepository.findByIdInAndDeletedFalse(ids);
    }

    // 論理削除されていないものだけ取得
    public List<ReturnEntity> findAllNotDeleted() {
        return returnRepository.findByDeletedFalse();
    }
}