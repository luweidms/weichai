package com.youming.youche.table.domain.workbench;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-05-04
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class FinancialWorkbenchInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 车队id
     */
    private Long tenantId;

    private Long userInfoId;

            /**
            * 平台剩余金额
            */
    private Long platformSurplusAmount;

            /**
            * 平台已用金额
            */
    private Long platformUsedAmount;

            /**
            * 剩余金额
            */
    private Long surplusAmount;

            /**
            * 油账户已用金额
            */
    private Long oilUsedAmount;

            /**
            * 油账户剩余金额
            */
    private Long oilSurplusAmount;

            /**
            * 应收账户 已收金额
            */
    private Long receivableReceivedAmount;

            /**
            * 应收账户 剩余金额
            */
    private Long receivableSurplusAmount;

            /**
            * 应付账户 已付金额
            */
    private Long payablePaidAmount;

            /**
            * 应付账户 已付金额
            */
    private Long payableSurplusAmount;

            /**
            * 应收逾期金额
            */
    private Long overdueReceivablesAmount;

            /**
            * 应付逾期金额
            */
    private Long overduePayableAmount;

            /**
            * 今日充值金额
            */
    private Long rechargeTodayAmount;

            /**
            * 待付款金额
            */
    private Long pendingPaymentAmount;

}
