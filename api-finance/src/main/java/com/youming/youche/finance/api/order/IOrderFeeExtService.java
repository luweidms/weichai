package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderFeeExt;

import java.util.List;

/**
* <p>
    * 订单费用扩展表 服务类
    * </p>
* @author liangyan
* @since 2022-03-15
*/
    public interface IOrderFeeExtService extends IBaseService<OrderFeeExt> {
    /**
     *  通过orderId 查询order_fee_ext数据
     * @param orderId
     * @return
     * @throws Exception
     */
        OrderFeeExt getOrderFeeExtByOrderId(Long orderId)throws Exception;

        List<OrderFeeExt> selectFee(Long id, Long tenantId,String beginDate, String endDate);
    }
