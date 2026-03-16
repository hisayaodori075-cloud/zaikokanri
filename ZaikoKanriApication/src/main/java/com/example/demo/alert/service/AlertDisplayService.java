package com.example.demo.alert.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.alert.entity.AlertSettingEntity;
import com.example.demo.alert.repository.AlertSettingRepository;
import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;

@Service
public class AlertDisplayService {

    @Autowired
    private ProductService productService;

    @Autowired
    private AlertSettingRepository alertSettingRepository;

    /**
     * 最低在庫アラート対象の商品取得
     */
    public List<ProductEntity> findMinStockAlert() {

        List<ProductEntity> productList = productService.findAll();

        Map<Integer, AlertSettingEntity> alertMap = getAlertMap();

        return productList.stream()
                .filter(product -> {

                    AlertSettingEntity setting = alertMap.get(product.getId());

                    if (setting == null) {
                        return false;
                    }

                    return product.getStock() < setting.getMinStock();

                })
                .toList();
    }

    /**
     * AlertSettingをMapで取得
     */
    public Map<Integer, AlertSettingEntity> getAlertMap() {

        return alertSettingRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        AlertSettingEntity::getProductId,
                        a -> a
                ));
    }

}