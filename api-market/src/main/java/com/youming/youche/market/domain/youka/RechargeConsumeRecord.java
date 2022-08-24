package com.youming.youche.market.domain.youka;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 油卡充值/消费(返利)记录表
    * </p>
* @author XXX
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class RechargeConsumeRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 卡来源服务商id
            */
    private Long serviceUserId;

            /**
            * 服务商名称
            */
    private String serviceName;

            /**
            * 卡号来源站点id
            */
    private Long productId;

            /**
            * 消费时间
            */
    private LocalDateTime consumeDate;

            /**
            * 卡号
            */
    private String cardNum;

            /**
            * 卡号申请时是否开票：0不开票，1开票
            */
    private Integer isNeedBill;

            /**
            * 卡类型：1中石油，2中石化
            */
    private Integer cardType;

            /**
            * 来源类型：1供应商油卡，2自购油卡，3客户油卡
            */
    private Integer sourceType;

            /**
            * 车牌号
            */
    private String plateNumber;

            /**
            * 持卡人
            */
    private String cardHolder;


            /**
            * 记录类型：1充值，2油卡消费
            */
    private Integer recordType;

            /**
            * 充值/消费金额（单位分）
            */
    private Long amount;

            /**
            * 余额（单位分）
            */
    private Long balance;

            /**
            * 加油量（毫升）
            */
    private Long oilRise;

            /**
            * 单价（单位分）
            */
    private Long unitPrice;

            /**
            * 奖励积分
            */
    private Long integral;

            /**
            * 服务商返利金额（单位分）
            */
    private Long serviceRebateAmount;

            /**
            * 车队返利金额（单位分）
            */
    private Long fleetRebateAmount;

            /**
            * 平台返利金额（单位分）
            */
    private Long platformRebateAmount;

            /**
            * 商品类别：
            */
    private String goodsType;

            /**
            * 商品名称
            */
    private String goodsName;

            /**
            * 记录类型导入数据
            */
    private String recordingName;

            /**
            * 消费地点
            */
    private String consuemStation;

            /**
            * 订单号
            */
    private Long orderId;

            /**
            * 返利分摊到的订单
            */
    private String rebeatShareOrder;

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

//            /**
//            * 创建时间
//            */
//    private LocalDateTime createDate;
//
//            /**
//            * 修改时间
//            */
//    private LocalDateTime updateDate;

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
    private Integer voucherState;

            /**
            * 处理描述
            */
    private String voucherRemark;
    /**
     * 凭证时间
     */
    private LocalDateTime voucherTime;

            /**
            * 代金券表id
            */
    private Long voucherId;

            /**
            * null初始化，1成功，2失败
            */
    private Integer monthBillState;

            /**
            * 处理描述
            */
    private String monthBillRemark;
    /**
     * 处理时间
     */
    private LocalDateTime monthBillTime;

            /**
            * 月账单表id
            */
    private Long monthBillId;

            /**
            * 数据来源：1车队，2服务商
            */
    private Integer recordSource;

            /**
            * null初始化，1成功，2失败，3手动停止对该数据处理
            */
    private Integer matchState;

            /**
            * 处理描述
            */
    private String matchRemark;
    /**
     * 处理时间
     */
    private LocalDateTime matchTime;

    @TableField(exist = false)
    /**失败原因*/
    private String reasonFailure;

}
