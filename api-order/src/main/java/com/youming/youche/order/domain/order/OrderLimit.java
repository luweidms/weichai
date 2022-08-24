package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 订单付款回显表
    * </p>
* @author CaoYaJie
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderLimit extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单id
            */
    private Long orderId;

            /**
            * 司机id
            */
    private Long userId;

            /**
            * 司机姓名
            */
    private String userName;

            /**
            * 手机号
            */
    private String userPhone;

            /**
            * 车辆归属（资金渠道）
            */
    private String vehicleAffiliation;

            /**
            * 账户可提现金额
            */
    private Long accountBalance;

            /**
            * 订单总金额
            */
    private Long totalFee;

            /**
            * 订单现金
            */
    private Long orderCash;

            /**
            * 已付预付款现金(已付)
            */
    private Long paidCash;

            /**
            * 应付预付款现金(未付)
            */
    private Long noPayCash;

            /**
            * 现金未提现金额
            */
    private Long noWithdrawCash;

            /**
            * 现金已提现金额
            */
    private Long withdrawCash;

            /**
            * 订单油（虚拟+实体）
            */
    private Long orderOil;

            /**
            * 实体油
            */
    private Long orderEntityOil;

            /**
            * 已付油卡金额(已付)
            */
    private Long paidOil;

            /**
            * 应付油卡金额(未付)
            */
    private Long noPayOil;

            /**
            * 订单油利润
            */
    private Long oilIncome;

            /**
            * 油卡未提现金额
            */
    private Long noWithdrawOil;

            /**
            * 油卡提现金额
            */
    private Long withdrawOil;

            /**
            * 订单etc
            */
    private Long orderEtc;

            /**
            * 已付etc金额(已付)
            */
    private Long paidEtc;

            /**
            * 应付etc金额(未付)
            */
    private Long noPayEtc;

            /**
            * 订单etc利润
            */
    private Long etcIncome;

            /**
            * 欠款金额
            */
    private Long debtMoney;

            /**
            * 已付欠款
            */
    private Long paidDebt;

            /**
            * 未付欠款
            */
    private Long noPayDebt;

            /**
            * 订单尾款
            */
    private Long orderFinal;

            /**
            * 已付尾款(已付)
            */
    private Long paidFinalPay;

            /**
            * 应付尾款(未付)
            */
    private Long noPayFinal;

            /**
            * 订单尾款利润
            */
    private Long finalIncome;

            /**
            * 尾款计划处理时间:签收时间+帐期
            */
    private LocalDateTime finalPlanDate;

            /**
            * 处理状态:0初始1完成2失败
            */
    private Integer fianlSts;

            /**
            * 到期类型：0自动到期；1手动到期
            */
    private Integer expireType;

            /**
            * 处理备注
            */
    private String stsNote;

            /**
            * 处理时间
            */
    private LocalDateTime stsDate;

            /**
            * 排序时间（预计靠台时间）
            */
    private LocalDateTime orderDate;

            /**
            * 创建时间
            */
    private LocalDateTime createDate;

    private Long tenantId;

            /**
            * 借支金额
            */
    private Long loanAmount;

            /**
            * 已核销借支金额
            */
    private Long verificationLoan;

            /**
            * 未核销借支金额
            */
    private Long noVerificationLoan;

            /**
            * 油卡抵押金额
            */
    private Long pledgeOilcardFee;

            /**
            * 车辆固定成本
            */
    private Long carCost;

            /**
            * 司机固定成本
            */
    private Long driverCost;

            /**
            * 司机未付固定成本
            */
    private Long driverCostNopay;

            /**
            * 司机已付固定成本
            */
    private Long driverCostPaid;

    private Long costEntityOil;

            /**
            * 订单路歌核销状态：null未核销，0核销成功，其余状态(1、2、3)核销失败
            */
    private Integer verificationState;

    private String verificationRemark;

            /**
            * 核销时间
            */
    private LocalDateTime verificationTime;

            /**
            * 报销费用
            */
    private Long expenseFee;

            /**
            * 已付维修保养费
            */
    private Long paidRepair;

            /**
            * 维修保养费利润
            */
    private Long repairIncome;

            /**
            * 服务商未提现维修费
            */
    private Long noWithdrawRepair;

            /**
            * 服务商已提现维修费
            */
    private Long withdrawRepair;

            /**
            * 订单来源单号
            */
    private Long sourceOrderId;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 是否需要开票 0无需 1需要
            */
    private Integer isNeedBill;

            /**
            * 修改时间
            */
    private LocalDateTime updateDate;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * 资金(油)渠道类型
            */
    private String oilAffiliation;

            /**
            * 未到期转到期金额
            */
    private Long marginTurn;

            /**
            * 预支未到期金额
            */
    private Long marginAdvance;

            /**
            * 油转现金额
            */
    private Long oilTurn;

            /**
            * etc转现金额
            */
    private Long etcTurn;

            /**
            * 未到期结算对账单金额
            */
    private Long marginSettlement;

            /**
            * 未到期抵扣对账单金额
            */
    private Long marginDeduction;

            /**
            * 油卡押金释放金额
            */
    private Long releaseOilcardFee;

            /**
            * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
            */
    private Integer oilAccountType;

            /**
            * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
            */
    private Integer oilBillType;

            /**
            * 到付款
            */
    private Long arriveFee;

            /**
            * 油费消费类型:1自有油站，2共享油站
            */
    private Integer oilConsumer;

    private Long noPayServiceFee;

            /**
            * 已付开票服务费
            */
    private Long paidServiceFee;

            /**
            * 收款人用户类型
            */
    private Integer userType;

            /**
            * 开票服务费
            */
    private Long serviceFee;


    /**
     * 匹配金额.
     */

    @TableField(exist = false)
    private Long matchAmount;

    /**
     * 匹配志鸿利润. todo
     */
    @TableField(exist = false)
    private Long matchIncome;
    /**
     * 匹配返现.
     */
    @TableField(exist = false)
    private Long matchBackIncome;

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
