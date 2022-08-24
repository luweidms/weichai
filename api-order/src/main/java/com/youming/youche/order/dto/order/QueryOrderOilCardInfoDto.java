package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/14 10:34
 */
@Data
public class QueryOrderOilCardInfoDto implements Serializable {

    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 油卡号
     */
    private String oilCardNum;

    /**
     * 油费
     */
    private Long oilFee;

    /**
     * 油卡类型
     */
    private Integer cardType;

    /**
     * 油卡渠道 0 添加 1 车辆绑定
     */
    private Integer cardChannel;

    /**
     * 油卡类型名称
     */
    private String cardTypeName;

    /**
     * 是否需要提醒
     */
    private Boolean isNeedWarn;

    /**
     * 理论余额
     */
    private Long cardBalance;

    /**
     * 油卡号
     */
    private String oilCarNum;

    /**
     * 车牌号
     */
    private String plateNumber;
}
