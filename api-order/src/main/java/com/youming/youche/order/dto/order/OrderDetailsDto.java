package com.youming.youche.order.dto.order;

import com.youming.youche.order.annotation.Translatable;
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

/**
 * @author hzx
 * @date 2022/3/23 16:47
 */
@Data
public class OrderDetailsDto implements Serializable {

    private static final long serialVersionUID = -1L;
    @Translatable
    private OrderInfo orderInfo;
    @Translatable
    private OrderInfoExt orderInfoExt;
    @Translatable
    private OrderFee orderFee;
    private OrderFeeExt orderFeeExt;
    @Translatable
    private OrderScheduler orderScheduler;
    @Translatable
    private OrderGoods orderGoods;
    private List<OrderOilDepotScheme> schemes;
    private Integer isHis;//1为历史   其他为现有
    private Integer orderDetailsType;//详情类型
    private Integer isTransit;//是否外发单
    private Integer updateState;//修改状态
    private Integer updateType;//修改类型
    @Translatable
    private OrderPaymentDaysInfo costPaymentDaysInfo;//成本账期
    @Translatable
    private OrderPaymentDaysInfo incomePaymentDaysInfo;//收入账期
    private OrderRetrographyCostInfo orderRetrographyCostInfo;//订单反写成本

    private List<OrderOilCardInfo> oilCardInfos;//油卡列表
    private List<OrderOilCardInfoDto> oilCardInfoDtos;//油卡列表
    @Translatable
    private List<OrderTransitLineInfo> transitLineInfos;//经停点

    private String toTenantAdminUserName;//转单车队超管名称
    private String toTenantAdminUserPhone;//转单车队超管手机号
    private String toTenantAdminUserId;//转单车队超管ID
    private String reciveAddr;
    private Integer provinceId;
    private Integer cityId;
    private String provinceName;
    private String cityName;
    private Long fixedCost;//固定成本
    private Long fee;//费用
    private Long operCost;//直接成本
    private Long changeCost;//变动成本
    private Long totalCost;//总成本 = 固定成本 + 费用 +  直接成本 +变动成本
    private Long totalOilFee;//总油费
    private List<Map> driverSubsidyDays;//补贴天数集合

    private Boolean orderIncomePermission;//成本查看权限
    private Boolean orderCostPermission;//收入查看权限

    public Long getTotalCost() {
        setTotalCost((fixedCost == null ? 0 : fixedCost) + (operCost == null ? 0 : operCost) + (fee == null ? 0 : fee) + (changeCost == null ? 0 : changeCost));
        return totalCost;
    }

}
