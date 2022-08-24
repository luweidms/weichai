package com.youming.youche.order.api.order.other;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
import com.youming.youche.order.domain.order.OrderFeeVer;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerVer;

public interface IOrderPayMethodService {
    /**
     * 外发单撤单接口
     * @param orderFee
     * @param scheduler
     * @param orderInfo
     * @throws Exception
     */
    void cancelTheOrderTransit(OrderFee orderFee, OrderScheduler scheduler, OrderInfo orderInfo,LoginInfo loginInfo,String token);

    /**
     * 修改外发单接口
     * @param userId
     * @param orderFee
     * @param orderFeeVer
     * @param orderInfo
     * @param orderInfoExt
     * @param orderScheduler
     * @param orderSchedulerVer
     * @param isUpdateDriver 是否修改司机
     * @throws Exception
     */
    void updateOrderTransit(Long userId, OrderFee orderFee, OrderFeeVer orderFeeVer, OrderInfo orderInfo,
                            OrderInfoExt orderInfoExt, OrderScheduler orderScheduler, OrderSchedulerVer orderSchedulerVer, OrderFeeExt orderFeeExt,
                            OrderFeeExtVer orderFeeExtVer, boolean isUpdateDriver ,LoginInfo loginInfo,String token);

}
