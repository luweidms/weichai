package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/24
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ConsumeOilFlowWxDto implements Serializable {
    /**
     * 费用
     */
    private Long amount;
    /**
     * 創建时间
     */
    private LocalDateTime createDate;
    private LocalDateTime getDate;
    /**
     * 对方用户名
     */
    private String otherName;
    /**
     * 流水号
     */
    private Long flowId;
    /**
     *
     */
    private Long oilPrice;
    /**
     * 账期
     */
    private int paymentDays;
    private String sourceFleet;
    /**
     * 状态
     */
    private Integer state;
    private String stateName;
    /**
     * 平台服务费金额(分)
     */
    private Long platformAmount;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 是否现场价加油 0不是，1是
     */
    private Integer localeBalanceState;
    /**
     * 流水号
     */
    private String flowIds;
    /**
     * 车队id
     */
    private Long tenantId;
}
