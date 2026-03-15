package com.example.demo.alert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alert_rotation_setting")
public class AlertRotationSettingEntity {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    // 注意期間
	    private Integer attentionDays;

	    // 緊急期間
	    private Integer urgentDays;

	    // 注意販売数
	    private Integer attentionSales;

	    // 緊急販売数
	    private Integer urgentSales;
	    
	    public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getAttentionDays() {
			return attentionDays;
		}

		public void setAttentionDays(Integer attentionDays) {
			this.attentionDays = attentionDays;
		}

		public Integer getUrgentDays() {
			return urgentDays;
		}

		public void setUrgentDays(Integer urgentDays) {
			this.urgentDays = urgentDays;
		}

		public Integer getAttentionSales() {
			return attentionSales;
		}

		public void setAttentionSales(Integer attentionSales) {
			this.attentionSales = attentionSales;
		}

		public Integer getUrgentSales() {
			return urgentSales;
		}

		public void setUrgentSales(Integer urgentSales) {
			this.urgentSales = urgentSales;
		}

}
