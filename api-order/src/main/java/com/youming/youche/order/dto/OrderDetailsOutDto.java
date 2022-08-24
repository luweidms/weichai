package com.youming.youche.order.dto;

import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.OrderRetrographyCostInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
public class OrderDetailsOutDto implements Serializable {


    private static final long serialVersionUID = -7852198593608825425L;

    private OrderInfo orderInfo;
    private OrderInfoExt orderInfoExt;
    private OrderFee orderFee;
    private OrderFeeExt orderFeeExt;
    private OrderScheduler orderScheduler;
    private OrderGoods orderGoods;
    private List<OrderOilDepotScheme> schemes;

    //1为历史   其他为现有
    private Integer isHis;

    //详情类型
    private Integer orderDetailsType;

    //是否外发单
    private Integer isTransit;

    //修改状态
    private Integer updateState;

    //修改类型
    private Integer updateType;

    //成本账期
    private OrderPaymentDaysInfo costPaymentDaysInfo;

    //收入账期
    private OrderPaymentDaysInfo incomePaymentDaysInfo;

    //订单反写成本
    private OrderRetrographyCostInfo orderRetrographyCostInfo;

    //油卡列表
    private List<OrderOilCardInfo> oilCardInfos;

    //经停点
    private List<OrderTransitLineInfo> transitLineInfos;

    //转单车队超管名称
    private String toTenantAdminUserName;

    //转单车队超管手机号
    private String toTenantAdminUserPhone;

    //转单车队超管ID
    private String toTenantAdminUserId;

    private String reciveAddr;
    /**
     * 省份id
     */
    private Integer provinceId;
    private Integer cityId;
    /**
     * 省份
     */
    private String provinceName;
    private String cityName;

    //固定成本
    private Long fixedCost;

    //费用
    private Long fee;

    //直接成本
    private Long operCost;

    //变动成本
    private Long changeCost;

    //总成本 = 固定成本 + 费用 +  直接成本 +变动成本
    private Long totalCost;

    //总油费
    private Long totalOilFee;

    //补贴天数集合
    private List<Map> driverSubsidyDays;

    //成本查看权限
    private Boolean orderIncomePermission;

    //收入查看权限
    private Boolean orderCostPermission;
}
