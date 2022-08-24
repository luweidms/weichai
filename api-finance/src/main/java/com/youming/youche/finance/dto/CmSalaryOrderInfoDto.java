package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CmSalaryOrderInfoDto implements Serializable {
   private Long salaryId;

   private Long orderId;

   private String beginDependTime;

   private String endDependTime;

   private Integer  sourceRegion;

   private Integer desRegion;

   private String customName;

   private String plateNumber;

   private Integer orderState;
}
