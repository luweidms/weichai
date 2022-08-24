package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleFeeFromOrderDataDto implements Serializable {

    private static final long serialVersionUID = -330055900832492349L;

    /**
     * 邮费
     */
    private Long oilFee;

    /**
     * 路桥费
     */
    private Long etcFee;

}
