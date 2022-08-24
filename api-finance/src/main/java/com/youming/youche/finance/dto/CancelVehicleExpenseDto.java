package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CancelVehicleExpenseDto implements Serializable {

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 车辆费用主键id
     */
    private Long vehicleExpenseId;
}
