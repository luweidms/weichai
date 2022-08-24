package com.youming.youche.market.domain.facilitator;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ConsumeOilFlow extends BaseDomain {

    private static final long serialVersionUID = 1L;


//    private Long flowId;
    /**
     * 对方账号(费用类型为2才有)
     */
    private Long accId;
    /**
     * 地址
     */
    private String address;
    /**
     * 预支手续费(分)
     */
    private Long advanceFee;
    /**
     * 加油金额(分)
     */
    private Long amount;
    /**
     * 加油使用现金金额(分)
     */
    private Long balance;
    /**
     * 费用类型 1：司机消费  2：油老板收入
     */
    private Integer costType;

//    private LocalDateTime createDate;
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
     * 是否评价 0未评价 1已评价(费用类型为1才有)',
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
     * 加油使用未到期金额(分)
     */
    private Long marginBalance;
    /**
     * 未核销平台金额(分)
     */
    private Long noVerificationAmount;
    /**
     * '资金(油)渠道类型',
     */
    private String oilAffiliation;
    /**
     * 加油使用油卡金额(分)
     */
    private Long oilBalance;
    /**
     * 加油价格(分/升)
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
     * 单号（订单、消费单等）
     */
    private String orderId;
    /**
     * 类型: 1:扫码加油 2:找油网加油
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
     * 平台金额(分)
     */
    private Long platformAmount;
    /**
     * 平台加油价格(分/升)
     */
    private Long platformPrice;
    /**
     * 产品id
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
     * 资金来源租户id(费用类型为2才有)
     */
    private Long tenantId;
    /**
     * 服务商未到期金额
     */
    private Long undueAmount;

//    private LocalDateTime updateDate;
    /**
     * 修改操作员id
     */
    private Long updateOpId;

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
     * 已核销平台金额(分)
     */
    private Long verificationAmount;


}
