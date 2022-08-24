package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RepairInfoVo implements Serializable {
    /**
     * 维修单号
     */
    private String repairCode;

    private String plateNumber;

    // 消费金额
    private Long totalFee;
    /**
     * 手续费
     */
    private Long serviceCharge;
    /**
     *维修时间
     */
    private String repairDate;

    /**
     * 维修单状态
     */
    private Integer repairState;

    private String repairStateName;

    private String name;


    /**
     * 服务商名
     */
    private String productName;

    /**
     * 维修单id
     */
    private Long repairId;

    private Long unexpired;

    private Long beExpired;

    private LocalDateTime deliveryDate;


}
