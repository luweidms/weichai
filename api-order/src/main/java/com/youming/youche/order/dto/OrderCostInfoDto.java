package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hzx
 * @date 2022/4/2 18:05
 */
@Data
public class OrderCostInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Date dependTime;
    private Long entityOilFee;
    private Long entityOilLitre;
    private Long virtualOilFee;
    private Long virtualOilLitre;
    private Long oilWearTotal;
    private Long oilFeeTotal;
    private Long pontageFee;
    private String orderLine;//订单总线路
    private Boolean isTransitLine;//是否有经停城市
    private Long emptyRunDistance;
    private Long runDistance;
    private Long oilRebate;//油返利

}
