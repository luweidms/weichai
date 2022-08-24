package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderVehicleTimeNode extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 补贴结束时间
     */
    private LocalDateTime endDate;
    /**
     * 截至订单号
     */
    private Long endOrderId;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 补贴开始时间
     */
    private LocalDateTime startDate;
    /**
     * 起始订单号
     */
    private Long startOrderId;



}
