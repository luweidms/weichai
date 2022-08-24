package com.youming.youche.order.domain.order;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 订单账单信息表
    * </p>
* @author CaoYaJie
* @since 2022-03-20
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderFeeStatement extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单编号
            */
    private Long orderId;

            /**
            * 应收款
            */
    private Long receivableAmount;

            /**
            * 确认应收款
            */
    private Long confirmAmount;

            /**
            * 实收日期
            */
    private LocalDateTime getAmoutDate;

            /**
            * 发票填写日期
            */
    private LocalDateTime receiptNumberDate;

            /**
            * 实收款（包含坏账）
            */
    private Long getAmount;

            /**
            * 发票号
            */
    private String receiptNumber;

            /**
            * 核销状态:0未核销1已核销2部分核销
            */
    private Integer financeSts;

            /**
            * 确认应收异常原因
            */
    private String confirmAmountNotes;

            /**
            * 实收异常原因
            */
    private String getAmountNotes;

            /**
            * 核销时间
            */
    private LocalDateTime financeDate;

            /**
            * 确认差异金额
            */
    private Long confirmDiffAmount;

            /**
            * 财务备注
            */
    private String financeNotes;

            /**
            * 迟到费用
            */
    private Long lateFee;

            /**
            * 账单号生成日期
            */
    private LocalDateTime billNumberDate;

            /**
            * 账单号
            */
    private String billNumber;

            /**
            * 操作员ID
            */
    private Long opId;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 修改数据的操作人id
            */
    private Long updateOpId;

            /**
            * 订单对应金额通过平安账户打款成功的核销金额
            */
    private Long sourceCheckAmount;

            /**
            * 实收（不包含坏账）
            */
    private Long realIncome;

            /**
            * 结算到期日(应付)
            */
    private LocalDateTime paySettleDueDate;

            /**
            * 结算到期日(应收)
            */
    private LocalDateTime receiveSettleDueDate;

            /**
            * 对账名称
            */
    private String checkName;


}
