package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ServiceOrderInfoDto implements Serializable {

    private static final long serialVersionUID = 6888978309618909289L;
    private Long id;
    private Long productId;
    private String stationId;
    /**
     * 车牌号
     */
    private String plateNumber;
    private Long userId;
    private String userName;
    private String userPhone;
    private Integer orderState;
    private Integer orderType;
    private Long oilLitre;
    private Long oilPrice;
    private Long oilFee;
    private String oilsId;
    private String oilsType;
    private String oilsName;
    private String oilsLevel;
    private String orderId;
    private Long createUserId;
    /**
     * 油账户明细 返回 值
     */
    private Long oilBalance;
    private Long cashBalance;

    private String orderStateName;
    private String orderTypeName;

    private String taskResult;
    private Integer taskState;
    private Date taskDate;
}
