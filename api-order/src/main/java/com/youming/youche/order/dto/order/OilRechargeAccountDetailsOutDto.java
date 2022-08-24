package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import lombok.Data;

@Data
public class OilRechargeAccountDetailsOutDto extends OilRechargeAccountDetails {
    private String sourceTypeName;
    private Double accountBalanceDouble;
    private Double distributedAmountDouble;
    private Double unUseredBalanceDouble;
    private String acctName;
    private String acctNo;
    private String tenantName;
    private String tenantTypeName;
    private String tenantAcctName;
    private String tenantAcctNo;
    private String tenantBillId;
    private String billId;
    private String billTypeName;
}
