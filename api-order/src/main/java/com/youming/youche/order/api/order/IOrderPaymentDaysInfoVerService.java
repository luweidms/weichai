package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderPaymentDaysInfoVerService extends IBaseService<OrderPaymentDaysInfoVer> {

    /**
     * 获取订单收入账期
     * @param orderId
     * @param paymentDaysType
     * @param isUpdate
     * @return
     */
    List<OrderPaymentDaysInfoVer> queryOrderPaymentDaysInfoVer(Long orderId, Integer paymentDaysType, Integer isUpdate);


    /**
     * 保存订单账期
     * @param info
     */
    void saveOrUpdateOrderPaymentDaysInfoVer(OrderPaymentDaysInfoVer info);

    /**
     * 失效上次修改订单未处理账期
     * @param orderId
     */
    void loseEfficacyPaymentDaysVerUpdate(Long orderId);

    /**
     * 版本赋值原表
     * @param oldObj
     * @param newObj
     * @throws Exception
     */
    void setOrderPaymentDaysInfoVer(OrderPaymentDaysInfo oldObj,OrderPaymentDaysInfo newObj);
}
