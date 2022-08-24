package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderLimitDto implements Serializable {
    private Long orderId;//订单号
    private Boolean isAddProblem; // 是否有尾款  fales 没有 true  有
}
