package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CheckConflictDto implements Serializable {

    private static final long serialVersionUID = -7280004066224920219L;

    /**
     * 靠台时间
     */
    private String dependTime;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 主驾id
     */
    private Long carDriverId;
    /**
     * 副驾id
     */
    private Long copilotDriverId;

    private Long fromOrderId;
    /**
     * 订单id
     */
    private Long orderId;
}
