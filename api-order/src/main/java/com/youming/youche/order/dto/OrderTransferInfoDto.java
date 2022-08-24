package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderTransferInfoDto implements Serializable {
    /**
     * 车队id
     */
    private Long tenantId;
    private String tenantName;
    private Long orderId;
    private String transferTenantName;
    /**
     * 始发省
     */
    private Integer sourceProvince;
    private String sourceRegionName;
    private Integer sourceRegion;
    private Integer sourceCounty;
    private Integer desProvince;
    private String desRegionName;//到达市
    private Integer desRegion;//到达市
    /**
     * 到达县
     */
    private Integer desCounty;
    // 转单状态 transferOrderState 转单状态(0待接单 1 已接单 2 已拒接 3 已超时)
    private String transferOrderState;
    private List<String> transferOrderStates;

    private List<OrderTransitLineInfo> transitLineInfos;

    private LocalDateTime transferDate;
    private Long totalFee;
    private String source;
    private String des;
    private String plateNumber;
    /**
     * 新订单号
     */
    private Long transferOrderId;
    private Integer isHis;
    private LocalDateTime dependTime;





}
