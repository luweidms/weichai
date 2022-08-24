package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.domain.order.UserRepairMargin;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ParametersNewDto implements Serializable {
    private Long userId;
    private String billId;
    private Long businessId;
    private Long orderId;
    private Long amount;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;
    private String finalPlanDate;
    private String totalFee;
    private Long tenantUserId;
    private String tenantBillId;
    private String tenantUserName;
    private Object obj;
   // private OrderLimit orderLimit;
    private List<OrderLimit> orderLimit;
    private List<OrderOilSource> sourceList;
    private Integer isNeedBill;
    private Long serviceUserId;
    private Long productId;
    private OrderOilSource oilSource;
    private ConsumeOilFlowExt consumeOilFlowExt;
    private ConsumeOilFlow consumeOilFlow;
    private RechargeOilSource rechargeOilSource;
    /**
     * 批次号
     */
    private String batchId;
    private Long accountDatailsId;
    private Long tenantId;
    private Integer advanceType;
    /**
     * 流水号
     */
    private Long flowId;
    /**
     * 转现类型(1油转现，2etc转现)
     */
    private String turnType;
    private PayoutIntf payoutIntf;
    private Integer carUserType;
    private String success;
    private String zhangPingType;
    private String type;
    private String tenantUserBill;
    private Long oaLoanId;
    private SysTenantDefDto sysTenantDef;
    private Long expenseId;
    private  Integer expenseType;
    private Long salaryId;
    private OrderLimit orderLimitBase;
    private Long otherFlowId;
    private Long payFlowId;
    private String oilAffiliation;
    private String sign;
    private List<UserRepairMargin> UserRepairMarginlist;
    private Long repairId;
    private String faceBalanceUnused;
    private String faceMarginUnused;
    private Integer userType;
    /**
     * 账户类型 1母卡 2子卡
     */
    private String accountType;
    private String isDriverOrderType;
    private Integer isOwnCar;
    private Long etcId;
    private String withdrawals;
    private Integer loanSubjects;
    private Integer loanTransReason;
    private Integer isDebt;
    private String salaryMonth;
}
