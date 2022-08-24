package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;

import java.util.List;

/**
 * <p>
 * 订单途径表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
public interface IOrderTransitLineInfoService extends IBaseService<OrderTransitLineInfo> {

    /**
     * 继承来源订单的经停点
     * @param orderId
     * @return
     */
    List<OrderTransitLineInfo> queryOrderTransitLineInfoByOrderId(Long orderId);

    /**
     * 删除原始经停点
     * @param orderId
     * @return
     */
    String deleteOrderTransitLineInfo(Long orderId);

    /**
     * 根据位置查询经停点
     * @param orderId
     * @param sortId
     * @param eand
     * @param nand
     * @return
     * @throws Exception
     */
    OrderTransitLineInfo queryTransitLineInfoByLocation(Long orderId,Integer sortId,String eand,String nand);

    /**
     * 获取需轨迹变更经停点
     * @param orderId 订单号
     * @param trackType 轨迹类型
     */
    OrderTransitLineInfo queryTrackTransitLineInfo(Long orderId, Integer trackType);

}
