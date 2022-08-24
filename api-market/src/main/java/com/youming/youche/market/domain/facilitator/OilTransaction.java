package com.youming.youche.market.domain.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OilTransaction implements Serializable {
    private static final long serialVersionUID = -3281418615869606867L;
    private String orderId; // 单号（订单、消费单等）
    private String tradeTimeStart; // 交易时间
    private String tradeTimeEnd; // 交易时间
    private String consumerName; // 对方用户名
    private String consumerBill; // 对方手机号码
    private String settlTimeStart; // 油老板帐期，用于油老板未到期转可用
    private String settlTimeEnd; // 油老板帐期，用于油老板未到期转可用
    private Integer state; // 0未到期，1已到期，2未到期转已到期失败
    private Long productId; // 产品id
    private Integer serviceType; // 付款人用户类型

}
