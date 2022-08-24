package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/4/28 10:31
 */
@Data
public class ServiceUnexpiredDetailDto implements Serializable {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 消费金额
     */
    private Long amount;

    /**
     * 服务商未到期金额
     */
    private Long undueAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 账期 用于未到期可转
     */
    private LocalDateTime getDate;

    /**
     *
     */
    private String name;

    /**
     * 使用未到期金额(分)
     */
    private Long marginBalance;
}
