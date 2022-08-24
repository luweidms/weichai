package com.youming.youche.finance.dto;

import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.RechargeOilSource;
import lombok.Data;

import java.io.Serializable;
@Data
public class LimitAndSourceDto implements Serializable {
    private OrderLimit orderLimit;
    private RechargeOilSource rechargeOilSource;
}
