package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilRechargeAccountDetailsFlow extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;

    /**
     *交易金额
     */
    private Long amount;
    /**
     *充值的银行卡名称
     */
    private String bankAccName;
    /**
     *批次号
     */
    private Long batchId;
    /**
     *业务单号
     */
    private String busiCode;
    /**
     *业务类型名称
     */
    private String busiName;
    /**
     *业务类型 1充值 2提现 3分配 4撤回 5加油 6返利 7还款 8移除-专票账户
     */
    private Integer busiType;
    /**
     *渠道标识
     */
    private String channelType;
    /**
     *当前金额（分）
     */
    private Long currentAmount;
    /**
     *匹配金额
     */
    private Long matchAmount;
    /**
     *操作员Id
     */
    private Long opId;
    /**
     *订单号(业务单号)
     */
    private String orderNum;
    /**
     *关联批次号
     */
    private String otherBatchId;
    /**
     *充值的平安虚拟账户
     */
    private String pinganAccId;
    /**
     *油账户id（oil_recharge_account_details主键）
     */
    private Long relId;
    /**
     *备注
     */
    private String remark;
    /**
     *资金来源
     */
    private Integer sourceType;
    /**
     *业务科目Id
     */
    private Long subjectsId;
    /**
     *租户Id
     */
    private Long tenantId;
    /**
     *匹配剩余金额
     */
    private Long unMatchAmount;
    /**
     *修改操作员Id
     */
    private Long updateOpId;
    /**
     *用户编号
     */
    private Long userId;
    /**
     *核销状态 0待确认 1已完成 2已核销
     */
    private Integer verifyState;


}
