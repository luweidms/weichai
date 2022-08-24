package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/19 9:52
 */
@Data
public class GetOilCardByCardNumDto implements Serializable {

    /**
     * 油卡id
     */
    private Long cardId;

    /**
     * 实体油卡卡号
     */
    private String oilCarNum;

    /**
     * 服务商名称
     */
    private String companyName;

    /**
     * 用户编码
     */
    private Long serviceUserId;

    /**
     * 油卡状态
     */
    private Integer oilCardStatus;

    /**
     * 油卡状态名称
     */
    private String oilCardStatusName;

    /**
     * 当前抵押订单号
     */
    private Long pledgeOrderId;

    /**
     * 产品
     */
    private Long productId;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    /**
     * 卡类型(1中石油 2中石化)
     */
    private Integer oilCardType;

    /**
     * 卡类型名称
     */
    private String oilCardTypeName;

    /**
     * 4待交付 5已交付 6已完成
     */
    private Integer servState;

    /**
     * 用户编码
     */
    private Long userId;

    /**
     * 油卡类型
     */
    private Integer cardType;

    /**
     * 油卡类型名称
     */
    private String cardTypeName;

    /**
     * 理论余额
     */
    private Long cardBalance;

    /**
     * 理论余额金额
     */
    private String cardBalanceStr;

    /**
     * 车牌号
     */
    private String plateNumber;
}
