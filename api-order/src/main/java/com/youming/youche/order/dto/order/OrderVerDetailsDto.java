package com.youming.youche.order.dto.order;

import com.youming.youche.order.annotation.Translatable;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.OrderRetrographyCostInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
import com.youming.youche.order.domain.order.OrderFeeVer;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsVer;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtVer;
import com.youming.youche.order.domain.order.OrderInfoVer;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilCardInfoVer;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerVer;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoVer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/3/23 16:47
 */
@Data
public class OrderVerDetailsDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private OrderInfoVer orderInfoVer;

    private OrderInfoExtVer orderInfoExtVer;

    private OrderFeeVer orderFeeVer;

    private OrderFeeExtVer orderFeeExtVer;

    private OrderSchedulerVer orderSchedulerVer;

    private OrderGoodsVer orderGoodsVer;

    private List<OrderOilDepotScheme> schemes;

    private OrderPaymentDaysInfoVer costPaymentDaysInfoVer;

    private OrderPaymentDaysInfoVer incomePaymentDaysInfoVer;

    private List<OrderOilCardInfoVer> oilCardInfos;//油卡列表

    private List<OrderOilCardInfoVerDto> oilCardInfoDtos;

    private Long totalOilFee;//总油费

    private List<OrderTransitLineInfoVer> transitLineInfoVers;//经停点

    private List<Map> driverSubsidyDays;//补贴天数集合


}
