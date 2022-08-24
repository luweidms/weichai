package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRepairInfoDto implements Serializable {
    private  Long id;
    private Double amountAll;
    private Double amountUse;

    private String orderId;
    private String createTime;
    private Double amount;
    private String otherName;
    private String otherUserBill;
    private Integer provinceId;
    private Integer cityId;
    private Integer countyId;
    private String address;
    private String getDate;
    private Long repairId;
    private Integer state;
    private String stateName;
    private String repairCode;
    private String plateNumber;

    private Integer stateInfo;
}
