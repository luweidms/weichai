package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单调度  借支新增  订单列表返回值
 */
@Data
public class OrderSchedulerDto implements Serializable {

    /**
     *订单号
     */
    private Long orderId;

    /**
     *  司机id
     */
    private Long carDriverId;

    /**
     *司机姓名
     */
    private String carDriverMan;

    /**
     *司机手机号
     */
    private String carDriverPhone;

    /**
     * 车牌号
     */
    private String plateNumber;

    private Integer sourceRegion;//始发市

    private String sourceRegionName;//始发市

    private Integer desRegion;//到达市

    private String desRegionName;//到达市

}
