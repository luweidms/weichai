package com.youming.youche.market.dto.youca;

import com.youming.youche.market.domain.youka.RechargeConsumeRecord;
import lombok.Data;

@Data
public class RechargeConsumeRecordOut extends RechargeConsumeRecord {
    private String cardTypeName;
    private String sourceTypeName;
    private String recordTypeName;
    private String voucherStateName;
    private String monthBillStateName;
    private String recordSourceName;
    private String dealRemark;

    private Double amountDouble;
    private Double balanceDouble;
    private Double oilRiseDouble;
    private Double unitPriceDouble;
    private Double serviceRebateAmountDouble;
    private Double fleetRebateAmountDouble;
    private Double platformRebateAmountDouble;

    private String oilCardStatusName;
    private String oilCardTypeName;
    private  String productName;//油站名称
}
