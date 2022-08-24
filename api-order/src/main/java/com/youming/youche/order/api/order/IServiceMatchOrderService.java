package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.ServiceMatchOrder;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IServiceMatchOrderService extends IBaseService<ServiceMatchOrder> {

    /**
     *  获取服务商的订单油特殊记录
     * @param otherFlowId
     * @param userId
     * @param vehicleAffiliation
     * @param oilAffiliation 油渠道 0不开票 1开票
     * @param serviceType 1油老板，2维修商，3ETC
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<ServiceMatchOrder> getServiceMatchOrderByOtherFlowId(Long otherFlowId, Long userId, String vehicleAffiliation, String oilAffiliation, Long tenantId, int serviceType) ;

    /**
     * 获取订单油消费记录
     * @param orderId 订单号集合
     * @param beginDate 开始时间
     * @param endDate 结束时间
     */
    List<ServiceMatchOrder> getServiceMatchOrder(List<Long> orderId, Date beginDate, Date endDate, Long userId, Long tenantId);

    /**
     *  该函数的功能描述:查询服务商需要平台开票，并且未提现的订单，按时间排序
     * @param userId
     * @return
     */
    List<ServiceMatchOrder> getHaServiceMatchOrder(Long userId);

    /**
     * 获取服务商的订单油记录
     * @param serviceUserId
     * @param state 提现状态
     * @param vehicleAffiliation 资金渠道
     * @param oilAffiliation 油渠道 0不开票 1开票
     * @param tenantId 打款租户id
     * @return
     * @throws Exception
     */
    List<ServiceMatchOrder> getServiceMatchOrder(Long serviceUserId, int state, String vehicleAffiliation,String oilAffiliation, Long tenantId);

}
