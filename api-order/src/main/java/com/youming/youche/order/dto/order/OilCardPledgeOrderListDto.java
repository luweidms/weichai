package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/5/20 10:29
 */
@Data
public class OilCardPledgeOrderListDto implements Serializable {

    /**
     * 实体油卡卡号
     */
    private String oilCardNum;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机id
     */
    private Long carDriverId;

    /**
     * 司机
     */
    private String carDriverMan;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 客户单号（跟cutom_id的关系）
     */
    private String customOrderId;

    private Long amount;

    private Double amountDouble;
}
