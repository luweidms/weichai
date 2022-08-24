package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilRechargeAccount extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户余额
     */
    private Long accountBalance;
    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;
    /**
     * 手机号码
     */
    private String billId;
    /**
     *现金充值余额
     */
    private Long cashRechargeBalance;
    /**
     *渠道标识
     */
    private String channelType;
    /**
     *授信充值余额
     */
    private Long creditRechargeBalance;
    /**
     *继承余额
     */
    private Long inheritBalance;
    /**
     *运输专票余额
     */
    private Long invoiceOilBalance;
    /**
     *操作员Id
     */
    private Long opId;
    /**
     *提现中金额
     */
    private Long processingBalance;
    /**
     *返利充值余额
     */
    private Long rebateRechargeBalance;
    /**
     *租户Id
     */
    private Long tenantId;
    /**
     *转移账户余额
     */
    private Long transferOilBalance;
    /**
     *未还款金额
     */
    private Long unRepaymentBalance;
    /**
     *修改操作员Id
     */
    private Long updateOpId;
    /**
     *用户编号
     */
    private Long userId;
    /**
     *用户名
     */
    private String userName;


}
