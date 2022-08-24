package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderGoods;

import java.util.Set;

/**
 * <p>
 * 订单货物表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
public interface IOrderGoodsService extends IBaseService<OrderGoods> {

    /**
     * 根据订单号获取订单货物信息
     * @param orderId
     * @return
     */
    OrderGoods getOrderGoods(Long orderId);

    /***
     * @Description: 查询三方订单新订单号
     * @Author: luwei
     * @Date: 2022/6/25 00:14
     * @Param orderId:
     * @return: com.youming.youche.order.domain.order.OrderGoods
     * @Version: 1.0
     **/
    OrderGoods getCustomNumber(Long orderId);

    /**
     * 根据订单号获取订单货物信息
     * @param orderId
     * @return
     */
    OrderGoods selectByOrderId(Long orderId);

    /**
     * 获取1个货物信息
     */
    OrderGoods getOneGoodInfo(Long orderId);

    /**
     * 根据订单id获取订单货物记录
     *
     * @param orderId 订单id
     * @return
     */
    Set<OrderGoods> getOrderGoodsSet(Long orderId);

}
