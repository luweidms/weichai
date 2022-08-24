package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoVer;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderTransitLineInfoVerService extends IBaseService<OrderTransitLineInfoVer> {

    /**
     * 设置账期
     * @param orderId
     * @param isUpdate
     * @return
     */
    List<OrderTransitLineInfoVer> queryOrderTransitLineInfoByOrderId(Long orderId,Integer isUpdate);

    /**
     * 设置订单经停点信息
     * @param transitLineInfoVer
     * @param baseUser
     */
    void setOrderTransitLineInfo(OrderTransitLineInfoVer transitLineInfoVer, LoginInfo baseUser);

    /**
     * 失效上次修改经停点
     * @param orderId
     * @param isUpdateNew
     * @param isUpdate
     * @return
     */
    String updateTransitLineInfoVerType(Long orderId,Integer isUpdateNew,Integer isUpdate);

}
