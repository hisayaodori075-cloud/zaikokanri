package com.example.demo.alert.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.alert.entity.AlertRotationSettingEntity;
import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;
import com.example.demo.stock.repository.SalesRepository;
import com.example.demo.stock.service.SalesService;

@Service
public class RotationAlertDisplayService {

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesService salesService;

    @Autowired
    private AlertRotationSettingService rotationSettingService;
    
    @Autowired
    private SalesRepository salesRepository;

    public String getAlertLevel(ProductEntity product, AlertRotationSettingEntity setting) {

        int urgentDays = setting.getUrgentDays();
        int attentionDays = setting.getAttentionDays();

        int urgentSales = setting.getUrgentSales();
        int attentionSales = setting.getAttentionSales();

        double urgentRate = (double) urgentSales / urgentDays;
        double attentionRate = (double) attentionSales / attentionDays;

        int urgentCount =
                salesService.getSalesCountLastDays(product.getId(), urgentDays);

        double urgentActualRate =
                (double) urgentCount / urgentDays;

        // ★警告優先
        if (urgentActualRate < urgentRate) {
            return "URGENT";
        }

        int attentionCount =
                salesService.getSalesCountLastDays(product.getId(), attentionDays);

        double attentionActualRate =
                (double) attentionCount / attentionDays;

        if (attentionActualRate < attentionRate) {
            return "ATTENTION";
        }

        return null;
    }
    
    public List<ProductEntity> findRotationAlert() {

        List<ProductEntity> productList = productService.findAll();

        AlertRotationSettingEntity setting =
                rotationSettingService.getSetting();
        
        if (setting == null) {
            return List.of();
        }

        int attentionDays = setting.getAttentionDays();

        return productList.stream()
            .filter(product -> {

                // ★販売中チェック
                if (!"販売中".equals(product.getSalesStatus())) {
                    return false;
                }

                // ★削除除外
                if (product.isDeleted()) {
                    return false;
                }

                // ★新規登録ガード
                if (product.getCreatedAt() != null &&
                    product.getCreatedAt()
                        .isAfter(java.time.LocalDateTime.now().minusDays(30))) {
                    return false;
                }

                // ★最終入荷日ガード
                if (product.getLastArrivalDate() == null) {
                    return false;
                }

                if (product.getLastArrivalDate()
                        .isAfter(java.time.LocalDateTime.now().minusDays(attentionDays))) {
                    return false;
                }

                // ★ここを共通ロジックに置き換え
                return getAlertLevel(product, setting) != null;

            })
            .toList();
    }
    
    public Map<Integer, Integer> getSalesCountMap(List<ProductEntity> productList, Integer days) {

        Map<Integer, Integer> salesCountMap = new HashMap<>();

        // ★ガード（超重要）
        if (days == null || days <= 0) {
            return salesCountMap; // 空で返す
        }

        LocalDate startDate = LocalDate.now().minusDays(days);

        for (ProductEntity product : productList) {

            Integer salesCount =
                    salesRepository.getSalesCountSince(product.getId(), startDate);

            if (salesCount == null) {
                salesCount = 0;
            }

            salesCountMap.put(product.getId(), salesCount);
        }

        return salesCountMap;
    }
    
    public Map<Integer, String> getLastSalesDateMap(List<ProductEntity> productList) {

        Map<Integer, String> lastSalesDateMap = new HashMap<>();

        for (ProductEntity product : productList) {

            LocalDate lastDate =
                    salesRepository.findLastSalesDate(product.getId());

            if (lastDate != null) {
                lastSalesDateMap.put(product.getId(), lastDate.toString());
            } else {
                lastSalesDateMap.put(product.getId(), "販売なし");
            }
        }

        return lastSalesDateMap;
    }
    
    public long countUrgentAlert() {

        AlertRotationSettingEntity setting = rotationSettingService.getSetting();
        
        if (setting == null) {
            return 0L;
        }

        List<ProductEntity> productList = productService.findAll();

        int attentionDays = setting.getAttentionDays();

        return productList.stream()
                .filter(product -> {

                    // ★販売中チェック
                    if (!"販売中".equals(product.getSalesStatus())) return false;

                    // ★削除除外
                    if (product.isDeleted()) return false;

                    // ★新規登録ガード
                    if (product.getCreatedAt() != null &&
                        product.getCreatedAt()
                            .isAfter(java.time.LocalDateTime.now().minusDays(30))) {
                        return false;
                    }

                    // ★最終入荷日チェック
                    if (product.getLastArrivalDate() == null) return false;

                    if (product.getLastArrivalDate()
                            .isAfter(java.time.LocalDateTime.now().minusDays(attentionDays))) {
                        return false;
                    }

                    // ★ここを統一（最重要）
                    return "URGENT".equals(getAlertLevel(product, setting));

                })
                .count();
    }
}