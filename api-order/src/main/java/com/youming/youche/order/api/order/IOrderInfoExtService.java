package com.youming.youche.order.api.order;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.vo.OrderVerifyInfoOut;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 订单扩展表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
public interface IOrderInfoExtService extends IBaseService<OrderInfoExt> {

    /**
     * 根据订单号获取订单扩展记录
     * @param orderId
     * @return
     */
    OrderInfoExt getOrderInfoExt(Long orderId);

    /**
     * 获取订单的付款方式
     * @param orderId 订单号
     * @param tenantId 租户id
     * @return
     */
    OrderInfoExt  getOrderInfoPaymentWay(Long orderId,Long tenantId);

    /**
     * 根据订单号获取订单扩展记录
     * @param orderId
     * @return
     */
    OrderInfoExt selectByOrderId(Long orderId);

    /**
     * 校验开票信息
     * @param orderInfo
     * @param orderInfoExt
     * @param orderScheduler
     * @param orderFee
     * @param orderGoods
     * @param transitLineInfos
     * @param user
     */
    void verifyOrderNeedBill(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderScheduler orderScheduler,
                             OrderFee orderFee, OrderGoods orderGoods, List<OrderTransitLineInfo> transitLineInfos, LoginInfo user);
    /**
     * 获取订单校验信息
     * @param plateNumber 车牌号
     * @param orderId 过滤订单号
     * @param dependTime 靠台时间
     * @param orderStateLT 小于此订单状态
     * @param userId 用户ID
     * @param isQueryLastOrder 是否查上一单(否则查下一单)
     * @return
     * @throws Exception
     */
    OrderVerifyInfoOut queryOrderVerifyInfoOut(String plateNumber, Long orderId, LocalDateTime dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder);


    /**
     * 获取订单校验信息
     * @param plateNumber
     * @param orderId
     * @param dependTime
     * @param orderStateLT
     * @param userId
     * @param isQueryLastOrder
     * @return
     */
    OrderVerifyInfoOut getOrderVerifyInfoOut (String plateNumber, Long orderId, LocalDateTime dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder);

    /**
     * 获取订单校验信息
     * @param plateNumber
     * @param orderId
     * @param dependTime
     * @param orderStateLT
     * @param userId
     * @param isQueryLastOrder
     * @return
     */
    OrderVerifyInfoOut getOrderVerifyInfoOutH (String plateNumber, Long orderId, LocalDateTime dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder);


    /**
     * 获取订单预估到达时间
     * @param orderId
     * @param dependTime
     * @param carStartDate
     * @param arriveTime
     * @param isHis
     * @return
     * @throws Exception
     */
     LocalDateTime getOrderArriveDate(Long orderId,LocalDateTime dependTime,LocalDateTime carStartDate,
                                            Float arriveTime,Boolean isHis);
}
