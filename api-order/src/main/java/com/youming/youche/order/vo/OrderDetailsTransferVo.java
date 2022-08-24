package com.youming.youche.order.vo;

import com.youming.youche.order.annotation.Translatable;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.domain.OrderFee;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program two
 * @description: 接单详情返回
 * @author: qinyunfeng
 * @create: 2022/03/19 11:01
 */
@Data
@Builder
public class OrderDetailsTransferVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * TODO 补加注释
     */
    @Translatable
    private OrderInfo orderInfo;
    @Translatable
    private OrderFee orderFee;
    @Translatable
    private OrderScheduler orderScheduler;
    @Translatable
    private OrderGoods orderGoods;
    private OrderFeeExt orderFeeExt;
    /**
     * 收入账期
     */
    @Translatable
    private OrderPaymentDaysInfo incomePaymentDaysInfo;
    @Translatable
    private OrderInfoExt orderInfoExt;
    /**
     * 经停点
     */
    private List<OrderTransitLineInfo> transitLineInfos;
    private Boolean orderIncomePermission;
    private Boolean orderCostPermission;


}



