package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceProductOutDto  implements Serializable {
    //可在油站消费总金额(单位分)
    private Long  consumeOilBalance;
    private  Long consumeOilBalanceBilll;
    private  String address;
    private  Long  originalPrice;
    private  Long oilPriceBill ;
    private  Integer localeBalanceState;
    private  Long oilPrice;
    private Double oilPriceDouble;
    private  Integer isBillAbility;
    private  String oilName;
    private  Long oilId;
    private List<ProductNearByOutDto> outList;
    private  Long oilBalance;
    private  Integer repairCount;
}
