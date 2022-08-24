package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderIdsDto implements Serializable {


    private static final long serialVersionUID = 7991828408354435658L;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 订单扩展id
     */
    private Long orderInfoExtId;
    /**
     * 订单货物id
     */
    private Long orderGoodsId;
    /**
     * 订单费用扩展id
     */
    private Long orderFeeExtId;
    /**
     * 订单调度id
     */
    private Long orderSchedulerId;
    /**
     * 订单费用id
     */
    private Long orderFeeId;

}
