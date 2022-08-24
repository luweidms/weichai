package com.youming.youche.order.vo;

import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/16
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class DispatchOrderVo implements Serializable {
    private Long orderId;
    private OrderInfo orderInfo;
    private OrderInfoExt orderInfoExt;
    private OrderGoods orderGoods;
    private OrderFee orderfee;
    private OrderFeeExt orderFeeExt;
    private OrderScheduler orderScheduler;
    private List<OrderOilDepotScheme> orderOilDepotSchemes;
    private OrderPaymentDaysInfo costPaymentDaysInfo;
    private List<OrderOilCardInfo> orderOilCardInfos;


}
