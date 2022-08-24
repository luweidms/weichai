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
 * @date: 2022/5/17
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AcceptOrderVo implements Serializable {
    private OrderInfo orderInfo;
    private OrderFee orderFee;
    private OrderGoods orderGoods;
    private OrderInfoExt orderInfoExt;
    private OrderFeeExt orderFeeExt;
    private OrderScheduler orderScheduler;
    private OrderPaymentDaysInfo costPaymentDaysInfo;
    private List<OrderOilDepotScheme> depotSchemes;
    private List<OrderOilCardInfo> orderOilCardInfos;
}
