package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoH;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单收入账期表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
public interface IOrderPaymentDaysInfoService extends IBaseService<OrderPaymentDaysInfo> {

    /**
     * 获取订单收入账期
     * @param orderId
     * @param paymentDaysType
     * @return
     */
    OrderPaymentDaysInfo queryOrderPaymentDaysInfo(Long orderId, Integer paymentDaysType);

    /**
     * 保存订单账期
     * @param info
     */
    void saveOrUpdateOrderPaymentDaysInfo(OrderPaymentDaysInfo info);

    /**
     * 获取报账模式
     * @param orderId 订单id
     * @param tenantId 租户id
     * @return
     */
    List<OrderPaymentDaysInfo> getOrderPaymentDaysInfo (Long orderId , Long tenantId);

    /**
     * 版本赋值原表
     * @param oldObj
     * @param newObj
     * @throws Exception
     */
    void setOrderPaymentDaysInfo(OrderPaymentDaysInfo oldObj, OrderPaymentDaysInfoVer newObj);

    /**
     * 获取收款账期(为空就无需账期)
     * @param info
     * @return
     * @throws Exception
     */
    Integer calculatePaymentDays(OrderPaymentDaysInfo info, LocalDateTime dependDate, Integer calculateType);

    /**
     * 获取收款账期(为空就无需账期)
     * @param info
     * @return
     * @throws Exception
     */
    Integer calculatePaymentDaysH(OrderPaymentDaysInfoH info, Date dependDate, Integer calculateType);

}
