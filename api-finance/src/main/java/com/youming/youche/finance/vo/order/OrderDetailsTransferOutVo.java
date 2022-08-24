package com.youming.youche.finance.vo.order;

import com.youming.youche.finance.domain.order.OrderFeeExt;
import com.youming.youche.finance.domain.order.OrderGoods;
import com.youming.youche.finance.domain.order.OrderInfo;
import com.youming.youche.finance.domain.order.OrderInfoExt;
import com.youming.youche.finance.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.finance.domain.order.OrderScheduler;
import com.youming.youche.finance.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.OrderFee;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接单订单详情查询返回类
 */
@Data
public class OrderDetailsTransferOutVo implements Serializable {

    private static final long serialVersionUID = 5836154445815562036L;

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
