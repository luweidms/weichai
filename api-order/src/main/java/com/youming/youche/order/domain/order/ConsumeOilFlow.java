package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 消费油记录表
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ConsumeOilFlow extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 用户id
            */
    private Long userId;

            /**
            * 手机号码
            */
    private String userBill;

            /**
            * 用户名
            */
    private String userName;

            /**
            * 费用类型 1：司机消费  2：油老板收入
            */
    private Integer costType;

            /**
            * 单号（订单、消费单等）
            */
    private String orderId;

            /**
            * 加油金额(分)
            */
    private Long amount;

            /**
            * 加油价格(分/升)
            */
    private Long oilPrice;

            /**
            * 加油升数
            */
    private Float oilRise;

            /**
            * 平台金额(分)
            */
    private Long platformAmount;

            /**
            * 平台加油价格(分/升)
            */
    private Long platformPrice;

            /**
            * 是否核销 0未核销 1部分核销 2已核销
            */
    private Integer isVerification;

            /**
            * 已核销平台金额(分)
            */
    private Long verificationAmount;

            /**
            * 未核销平台金额(分)
            */
    private Long noVerificationAmount;

            /**
            * 对方用户id
            */
    private Long otherUserId;

            /**
            * 对方手机号码
            */
    private String otherUserBill;

            /**
            * 对方用户名
            */
    private String otherName;

            /**
            * 对方账号(费用类型为2才有)
            */
    private Long accId;

            /**
            * 资金渠道类型
            */
    private String vehicleAffiliation;

            /**
            * 资金来源租户id(费用类型为2才有)
            */
    private Long tenantId;

            /**
            * 产品id
            */
    private Long productId;

            /**
            * 0未到期，1已到期，2未到期转已到期失败
            */
    private Integer state;

            /**
            * 到期类型：0自动到期；1手动到期
            */
    private Integer expireType;

            /**
            * 服务商已到期金额
            */
    private Long expiredAmount;

            /**
            * 服务商未到期金额
            */
    private Long undueAmount;

            /**
            * 预支未到期手续费
            */
    private Long serviceCharge;

            /**
            * 油老板帐期，用于油老板未到期转可用
            */
    private LocalDateTime getDate;

            /**
            * 油老板帐期处理结果
            */
    private String getResult;

            /**
            * 加油是否需要开票 0无需 1需要
            */
    private Integer isNeedBill;

            /**
            * 油站开票折扣率
            */
    private String oilRateInvoice;

            /**
            * 产品名称
            */
    private String productName;

            /**
            * 服务电话
            */
    private String serviceCall;

            /**
            * 地址
            */
    private String address;

            /**
            * 找油网返回flow_id
            */
    private Long synFlowId;

            /**
            * 找油网返回时间
            */
    private LocalDateTime synDate;

            /**
            * 同步找油网金额
            */
    private Long synAccount;

            /**
            * 加油使用油卡金额(分)
            */
    private Long oilBalance;

            /**
            * 加油使用现金金额(分)
            */
    private Long balance;

            /**
            * 加油使用未到期金额(分)
            */
    private Long marginBalance;

            /**
            * 预支手续费(分)
            */
    private Long advanceFee;

            /**
            * 是否评价 0未评价 1已评价(费用类型为1才有)
            */
    private Integer isEvaluate;

            /**
            * 评价质量
            */
    private Integer evaluateQuality;

            /**
            * 评价价格
            */
    private Integer evaluatePrice;

            /**
            * 评价服务
            */
    private Integer evaluateService;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * 备注
            */
    private String remark;

            /**
            * 资金(油)渠道类型
            */
    private String oilAffiliation;

            /**
            * 车牌号
            */
    private String plateNumber;

            /**
            * 类型: 1:扫码加油 2:找油网加油
            */
    private Integer fromType;

            /**
            * 类型: 1:扫码加油 2:找油网加油
            */
    private String orderNum;

            /**
            * 批次号
            */
    private Long soNbr;

            /**
            * 是否现场价加油，0否、1是
            */
    private Integer localeBalanceState;

            /**
            * 收款人用户类型
            */
    private Integer userType;

            /**
            * 付款人用户类型
            */
    private Integer payUserType;

    @TableField(exist = false)
    private Long subjectsId;
    /**
     * 流水号
     */
    @TableField(exist = false)
    private Long flowId;
    /**
     * 流水号
     */
    @TableField(exist = false)
    private String flowIds;



    /**
     * 司机姓名
     */
    @TableField(exist = false)
    private String carDriverMan;

    /**
     * 代收人
     */
    @TableField(exist = false)
    private String collectionUserName;

    /**
     * 车辆类型
     */
    @TableField(exist = false)
    private Integer vehicleClass;
}
