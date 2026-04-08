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

    public List<ProductEntity> findRotationAlert() {

        List<ProductEntity> productList = productService.findAll();

        AlertRotationSettingEntity setting =
                rotationSettingService.getSetting();

        int attentionDays = setting.getAttentionDays();
        int urgentDays = setting.getUrgentDays();

        int attentionSales = setting.getAttentionSales();
        int urgentSales = setting.getUrgentSales();

        double attentionRate = (double) attentionSales / attentionDays;
        double urgentRate = (double) urgentSales / urgentDays;

        return productList.stream()
                .filter(product -> {

                    // ★販売中チェック（最優先）
                    if (!"販売中".equals(product.getSalesStatus())) {
                        return false;
                    }

                    // ★null対策（在庫ではなく売上）
                    int urgentCount =
                            salesService.getSalesCountLastDays(product.getId(), urgentDays);

                    int attentionCount =
                            salesService.getSalesCountLastDays(product.getId(), attentionDays);

                    double urgentActualRate =
                            urgentDays == 0 ? 0 : (double) urgentCount / urgentDays;

                    double attentionActualRate =
                            attentionDays == 0 ? 0 : (double) attentionCount / attentionDays;

                    // ★判定
                    return urgentActualRate < urgentRate
                            || attentionActualRate < attentionRate;

                })
                .toList();
    }
    
    public Map<Integer, Integer> getSalesCountMap(List<ProductEntity> productList, int days) {

        Map<Integer, Integer> salesCountMap = new HashMap<>();

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

        List<ProductEntity> productList = productService.findAll();

        return productList.stream()
                .filter(product -> {

                    // ★一覧と同じ条件を必ず入れる
                    if (!"販売中".equals(product.getSalesStatus())) {
                        return false;
                    }

                    int urgentCount =
                            salesService.getSalesCountLastDays(
                                    product.getId(),
                                    setting.getUrgentDays()
                            );

                    return urgentCount < setting.getUrgentSales();

                })
                .count();
    }
}