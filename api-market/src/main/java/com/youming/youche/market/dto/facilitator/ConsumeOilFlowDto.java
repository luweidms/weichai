package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeOilFlowDto implements Serializable {
   private Long id;
   private String orderId;
   private Double amount;
   private String amountStr;
   private Long oilPrice;
   private Double oilPriceStr;
   private String otherName;
   private String otherUserBill;
   private String plateNumber;
   private String address;
   private String getDate;
   private String createDate;
   private Integer state;
   private String stateName;
   private Double oilRise;

   private Double amountAll;
   private Double amountUse;
}
