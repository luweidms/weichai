package com.youming.youche.market.domain.facilitator.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OilTransaction {
    private String orderId; //订单id
    private String tradeTimeStart; // 交易时间开始
    private String tradeTimeEnd; // 交易时间结束
    private String consumerName; // 消费者姓名
    private String consumerBill; // 消费者账单
    private String settlTimeStart; // 结算时间开始
    private String settlTimeEnd; // 结算时间结束
    private Integer state; // 状态
    private Long productId; // 产品id
}
