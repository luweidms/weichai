package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderGoodsVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderGoodsVerService extends IBaseService<OrderGoodsVer> {
    /**
     * 通过orderid查询OrderGoodsVer
     * @param orderId
     * @return
     */
    OrderGoodsVer getOrderGoodsVer(Long orderId);

}
