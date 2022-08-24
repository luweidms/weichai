package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class OrderAccountOutVo  implements Serializable {
    //总现金
    private Long totalBalance;
    //总油（客户油）
    private Long totalOilBalance;
    // 共享客户油
    private Long totalShareBalance;
    // 自有客户油
    private Long totalHaveBalance;
    //总ETC
    private Long totalEtcBalance;
    //总未到期
    private Long totalMarginBalance;
    //总欠款金额
    private Long totalDebtAmount;
    //总维修基金
    private Long totalRepairFund;
    //冻结未到期金额
    private Long frozenMarginBalance;
    //冻结现金
    private Long frozenBalance;
    //冻结油
    private Long frozenOilBalance;
    //冻结etc
    private Long frozenEtcBalance;
    //冻结维修基金
    private Long frozenRepairFund;
    //可预支金额
    private Long canAdvance;
    //不可预支金额
    private Long cannotAdvance;
    //不可使用金额
    private Long cannotUseBalance;
    //可使用金额
    private Long canUseBalance;
    //可使用油
    private Long canUseOilBalance;
    //可使用ETC
    private Long canUseEtcBalance;
    //可使用维修基金
    private Long canUseRepairFund;
    //押金
    private Long depositBalance;
    //订单油账户
    private Long oilBalance;
    //充值油账户
    private Long rechargeOilBalance;
    //油充值账户
    private Long oilRechargeBalance;
    //已开票油账户
    private Long oilInvoicedBalance;

    //账户Id
    private Long sourceTenantId;
    //账户名称
    private String name;
    //账户状态
    private Integer accState;
    //账户状态名称
    private String accStateName;

    //即将到期状态名称
    private String marginStateName;
    //车队手机号码
    private String mobilePhone;
    //应收逾期
    private Long receivableOverdueBalance;
    //应付逾期
    private Long payableOverdueBalance;

    //收款账户余额(分)
    private Long incomeBalance;
    //付款账户余额(分)
    private Long payBalance;

    private Long privateReceivableAccount;//私人收款账户余额
    private Long privatePayableAccount;//私人付款账户余额
    private Long businessReceivableAccount;//对公收款账户余额
    private Long businessPayableAccount;//对公付款账户余额

    private Long businessAccount;//对公可提现
    private Long privateAccount;//对私可提现



    private int payrollNumber;//工资单待确认数量
    private int statementNumber;//对账单待确认数量

    private Boolean isUserBindCard;

    private long electronicOilCard;//电子油卡
    private long lockBalance;//预存资金
    private long creditLine;//授信额度
    private long usedCreditLine;//已用额度
}
