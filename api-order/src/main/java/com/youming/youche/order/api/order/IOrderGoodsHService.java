package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderGoodsH;

/**
 * <p>
 * 订单货物历史表 服务类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
public interface IOrderGoodsHService extends IBaseService<OrderGoodsH> {

    /**
     * 根据订单号获取历史订单货物信息
     * @param orderId
     * @return
     */
    OrderGoodsH selectByOrderId(Long orderId);

    /**
     * 根据订单id获取订单货物历史记录
     *
     * @param orderId 订单id
     * @return
     */
    OrderGoodsH getOrderGoodsH(Long orderId);

    /**
     * 根据订单id获取订单货物历史记录
     * @param orderId
     * @return
     */
    OrderGoodsH getOneGoodInfoH(Long orderId);

    /***
     * @Description: 查询三方订单新订单号
     * @Author: luwei
     * @Date: 2022/6/25 00:14
     * @Param orderId:
     * @return: com.youming.youche.order.domain.order.OrderGoods
     * @Version: 1.0
     **/
    OrderGoodsH getCustomNumberH(Long orderId);
}
