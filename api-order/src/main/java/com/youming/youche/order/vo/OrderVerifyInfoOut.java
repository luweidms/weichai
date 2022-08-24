package com.youming.youche.order.vo;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderVerifyInfoOut implements Serializable{
	private Long orderId;
	private LocalDateTime dependTime;
	private LocalDateTime carDependDate;
	private LocalDateTime carStartDate;
	private LocalDateTime carArriveDate;
	/**
	 * 到达时限
	 */
	private Float arriveTime;
	private String eand;
	private String eandDes;
	private String nand;
	private String nandDes;
	private Boolean isHis;
	private Integer sourceRegion;
	private Integer desRegion;
	private String sourceRegionName;
	private String desRegionName;


}
