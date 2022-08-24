package com.youming.youche.order.dto;

import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderDetailsTransferDto implements Serializable {
    private OrderInfo orderInfo;
    private OrderFee orderFee;
    private OrderScheduler orderScheduler;
    private OrderGoods orderGoods;
    private OrderFeeExt orderFeeExt;
    private OrderPaymentDaysInfo incomePaymentDaysInfo;//收入账期
    private OrderInfoExt orderInfoExt;
    private List<OrderTransitLineInfo> transitLineInfos;//经停点
    private Boolean orderIncomePermission;
    private Boolean orderCostPermission;


}
