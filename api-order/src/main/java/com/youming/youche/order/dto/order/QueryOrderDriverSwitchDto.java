package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/5/18 19:04
 */
@Data
public class QueryOrderDriverSwitchDto implements Serializable {

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

    /**
     * 切换状态：1-无切换记录 切换司机与当前登录账户司机一致  0-无切换记录  切换司机与当前登录账户司机不一致  2-有切换记录
     */
    private Integer switchType;

    /**
     * 切换司机ID
     */
    private Long onDutyDriverId;

    /**
     * 切换记录
     */
    private List<OrderDriverSwitchInfoDto> listOut;
}
