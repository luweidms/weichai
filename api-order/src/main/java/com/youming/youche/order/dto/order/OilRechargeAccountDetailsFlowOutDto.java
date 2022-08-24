package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import lombok.Data;

@Data
public class OilRechargeAccountDetailsFlowOutDto  extends OilRechargeAccountDetailsFlow {

    private static final long serialVersionUID = 2312601508438037612L;
    private Double currentAmountDouble;
    private Double amountDouble;
    private Double matchAmountDouble;
    private Double unMatchAmountDouble;
    private String userName;
    private String accName;
    private String operName;
    private String billId;

    private String tenantName;
    private String tenantBillId;

    private String tenantTypeName;
    private String busiTypeName;
    private String verifyStateName;
    private String pinganAcctName;
    private String pinganAcctNo;
    private String plateNumber;//车牌号
    private String relationName;//司机/车队名称
    private String relationPhone;//司机/车队账号
    private Boolean isOrderUnSkip;//订单不可跳转，如果是true不跳转，false或者null可以跳转
    private String billTypeName;//抵扣运输专票，自己收油品，--

}
