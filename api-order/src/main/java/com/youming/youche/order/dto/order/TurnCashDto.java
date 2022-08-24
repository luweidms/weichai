package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/5/20 17:30
 */
@Data
public class TurnCashDto implements Serializable {

    private Long userId;//司机ID
    private String vehicleAffiliation;//资金渠道类型
    private Long canTurnMoney;//可转移金额
    private Double canTurnMoneyDouble;
    private Long orderMoney;//订单油卡或ETC总金额
    private Double orderMoneyDouble;
    private Long consumeMoney;//消费总金额
    private Double consumeMoneyDouble;
    private Long turnDiscount;//转移折扣
    private Double turnDiscountDouble;
    private String turnDiscountString;
    private Long oaLoanOilAmount;//借支油
    private Double oaLoanOilAmountDouble;
    private Long turnBalance;//已转金额
    private Double turnBalanceDouble;
    private Long deductibleMargin;//待抵扣欠款
    private Double deductibleMarginDouble;
    private LocalDateTime createDate;
}
