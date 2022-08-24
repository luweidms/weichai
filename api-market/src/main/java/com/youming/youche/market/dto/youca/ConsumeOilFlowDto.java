package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author XXX
* @since 2022-03-17
*/
    @Data
    public class ConsumeOilFlowDto implements Serializable {

    /**
     * ID
     */
    private Long Id;

    /**
     * 对方账号
     */
    private Long accId;

    /**
     * 地址
     */
    private String address;

    /**
     * 预支手续费
     */
    private Long advanceFee;

    /**
     * 加油金额
     */
    private Long amount;

    /**
     * 加油使用现金金额
     */
    private Long balance;

    /**
     * 卡类型
     */
    private Long cardType;

    /**
     * 费用类型
     */
    private Integer costType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 评价价格
     */
    private Integer evaluatePrice;

    /**
     * 评价质量
     */
    private Integer evaluateQuality;

    /**
     * 评价服务
     */
    private Integer evaluateService;

    /**
     * 到期类型：0自动到期；1手动到期
     */
    private Integer expireType;

    /**
     * 服务商已到期金额
     */
    private Long expiredAmount;

    /**
     * 类型: 1:扫码加油 2:找油网加油
     */
    private Integer fromType;

    /**
     * 油老板帐期，用于油老板未到期转可用
     */
    private LocalDateTime getDate;

    /**
     * 油老板帐期处理结果
     */
    private String getResult;

    /**
     * 商品类型
     */
    private String goodsType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 是否评价 0未评价 1已评价
     */
    private Integer isEvaluate;

    /**
     * 加油是否需要开票 0无需 1需要
     */
    private Integer isNeedBill;

    /**
     * 是否核销 0未核销 1部分核销 2已核销
     */
    private Integer isVerification;

    /**
     * 是否现场价加油，0否、1是
     */
    private Integer localeBalanceState;

    /**
     * 加油使用未到期金额
     */
    private Long marginBalance;

    /**
     * 未核销金额
     */
    private Long noVerificationAmount;

    /**
     * 资金(油)渠道类型
     */
    private String oilAffiliation;

    /**
     * 加油使用油卡金额
     */
    private Long oilBalance;

    /**
     * 加油价格
     */
    private Long oilPrice;

    /**
     * 油站开票折扣率
     */
    private String oilRateInvoice;

    /**
     * 加油升数
     */
    private Float oilRise;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单数量
     */
    private String orderNum;

    /**
     * 对方用户名
     */
    private String otherName;

    /**
     * 对方手机号码
     */
    private String otherUserBill;

    /**
     * 对方用户id
     */
    private Long otherUserId;

    /**
     * 付款人用户类型
     */
    private Integer payUserType;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 平台金额
     */
    private Long platformAmount;

    /**
     * 平台加油价格
     */
    private Long platformPrice;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 服务电话
     */
    private String serviceCall;

    /**
     * 预支未到期手续费
     */
    private Long serviceCharge;

    /**
     * 批次号
     */
    private Long soNbr;

    /**
     * 0未到期，1已到期，2未到期转已到期失败
     */
    private Integer state;

    /**
     * 同步找油网金额
     */
    private Long synAccount;

    /**
     * 找油网返回时间
     */
    private LocalDateTime synDate;

    /**
     * 找油网返回flow_id
     */
    private Long synFlowId;

    /**
     * 车队ID
     */
    private Long tenantId;

    /**
     * 服务商未到期金额
     */
    private Long undueAmount;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新用户ID
     */
    private Long updateOpId;

    /**
     * 手机号码
     */
    private String userBill;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 收款人用户类型
     */
    private Integer userType;

    /**
     * 资金渠道类型
     */
    private String vehicleAffiliation;

    /**
     * 已核销平台金额
     */
    private Long verificationAmount;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 是否分享
     */
    private Integer isShare;

    /**
     * 返利金额
     */
    private Long rebateAmount;

    /**
     * 消费日期
     */
    private LocalDateTime consumeDate;

    /**
     * 持卡人
     */
    private String cardHolder;

    /**
     * 持卡人手机
     */
    private String cardHolderBill ;

    /**
     * 是否过期
     */
    private String isExpire;

    /**
     * 消费站点
     */
    private String consuemStation;

    /**
     * 来源类型名称
     */
    private String sourceTypeName;

    /**
     * 回扣金额
     */
    private String fleetRebateAmountDouble;

    /**
     * 油站类型名称
     */
    private String oilStationTypeName;

    /**
     * 卡号
     */
    private String cardNum;

    /**
     * 加油升数
     */
    private Float oilRiseDouble;

    /**
     * 单价
     */
    private String unitPriceDouble;

    /**
     * 加油金额
     */
    private String amountDouble;

    /**
     * 卡类型
     */
    private String cardTypeName;

    /**
     * 余额
     */
    private String balanceDouble;
}
