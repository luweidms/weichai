package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/5/18 18:51
 */
@Data
public class QueryOrderTitleBasicsInfoDto implements Serializable {

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 总线路
     */
    private String orderLine;

    /**
     * 订单状态0:待确认、1：正在交易、2：已确认、3：在运输、4完成
     */
    private Integer orderState;

    /**
     * 订单状态名称
     */
    private String orderStateName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;
}
