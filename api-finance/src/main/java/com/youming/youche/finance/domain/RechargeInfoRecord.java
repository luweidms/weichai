package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 充值记录表
    * </p>
* @author WuHao
* @since 2022-04-15
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class RechargeInfoRecord extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 充值流水号
            */
    private String rechargeFlowId;

            /**
            * 卡来源服务商id
            */
    private Long serviceUserId;

            /**
            * 卡号来源产品id
            */
    private Long productId;

            /**
            * 卡号
            */
    private String cardNum;

            /**
            * 卡类型：1中石油，2中石化
            */
    private Integer cardType;

            /**
            * 来源类型：1供应商油卡，2自购油卡，3客户油卡
            */
    private Integer sourceType;

            /**
            * 充值金额（单位分）
            */
    private Long rechargeAmount;

            /**
            * 代金券支付金额（单位分）
            */
    private Long voucherPayAmount;

            /**
            * 现金支付金额（单位分）
            */
    private Long payCash;

            /**
            * 待充金额（单位分）
            */
    private Long noRechargeAmount;

            /**
            * 状态：1待付款、2已付款、3服务商充值完成、4已充值、5已撤销
            */
    private Integer state;

            /**
            * 批次号
            */
    private Long soNbr;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 车队名称
            */
    private String tenantName;

            /**
            * 车队账户
            */
    private String tenantBill;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * null初始化，1成功，2失败
            */
    private Integer isReport;

            /**
            * 处理描述
            */
    private String reportRemark;

    private LocalDateTime reportTime;

            /**
            * 产品名称
            */
    private String productName;

            /**
            * 充值前截图
            */
    private String fileBeforeRecharge;

            /**
            * 充值后截图
            */
    private String fileAfterRecharge;

            /**
            * 充值类型：0常规充值，1服务商反向充值
            */
    private Integer rechargeType;


}
