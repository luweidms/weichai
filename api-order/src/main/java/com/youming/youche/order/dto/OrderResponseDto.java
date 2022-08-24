package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class OrderResponseDto implements Serializable {
    private List<OrderOilSource> sourceList;
    private OrderOilSource oilSource;
    private RechargeOilSource rechargeOilSource;
    private List<RechargeOilSource> rechargeOilSourceList;
    private OrderOilSource orderOilSource;
}
