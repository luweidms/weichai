package com.youming.youche.order.domain.order;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class AccountDetailsSummary extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 金额（分）
     */
    private Long amount;

    /**
     * 账本科目
     */
    private Long bookType;

    /**
     * 业务编码
     */
    private Long businessNumber;

    /**
     * 业务类型
     */
    private Integer businessTypes;

    /**
     * 费用类型 1：消费  2：充值
     */
    private Integer costType;

    /**
     * 当前金额（分）
     */
    private Long currentAmount;

    /**
     * 当前欠款金额
     */
    private Long currentDebtAmount;

    /**
     * 欠款金额
     */
    private Long debtAmount;

    /**
     * etc卡号
     */
    private String etcNumber;

    /**
     * 发生时间
     */
    private LocalDateTime happenDate;

    /**
     * 是否油老板提现0非油老板1是油老板
     */
    private Integer isOilPay;

    /**
     * 0未被报表收集1已经收集
     */
    private Integer isReported;

    /**
     * 备注
     */
    private String note;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 对方名称
     */
    private String otherName;

    /**
     * 对方用户编号
     */
    private Long otherUserId;

    /**
     * 收支途径
     */
    private String payWay;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 报表核销金额
     */
    private Long reportAmount;

    /**
     * 报表收集日期
     */
    private LocalDateTime reportDate;

    /**
     * 流水号
     */
    private Long soNbr;

    /**
     * 费用科目ID
     */
    private Long subjectsId;

    /**
     * 费用科目名称
     */
    private String subjectsName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 资金转移
     */
    private Long transferAmount;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收款人用户类型
     */
    private Integer userType;

    /**
     * 资金渠道类型
     */
    private String vehicleAffiliation;


}
