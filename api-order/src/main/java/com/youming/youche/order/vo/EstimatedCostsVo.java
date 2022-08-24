package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class EstimatedCostsVo implements Serializable {
    private Long carDriverId;
    private Long copilotUserId;
    private Long tenantId;
    /**
     * 到达时限
     */
    private Float arriveTime;
    /**
     * 车牌号
     */
    private String plateNumber;
    private Long distance;
    /**
     * 路桥费的单价
     */
    private Long pontagePer;
    private String nand;
    private String eand;
    private String region;
    /**
     * 省份id
     */
    private Long provinceId;
    private Long orderId;
    private Long emptyDistance;
    private String oilPrice;
    private Integer transitLineSize;
    private String dependTime;
    private Long loadEmptyOilCost;
    private Long loadFullOilCost;


}
