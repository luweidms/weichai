package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderGoods;

/**
* <p>
    * 订单货物表 服务类
    * </p>
* @author liangyan
* @since 2022-03-15
*/
public interface IOrderGoodsService extends IBaseService<OrderGoods> {

    /**
     * 通过orderId查询order goods
     * @param orderId
     * @return
     * @throws Exception
     */
    OrderGoods getOrderGoodsByOrderId(Long orderId)throws Exception;

}
