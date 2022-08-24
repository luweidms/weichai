package com.youming.youche.order.api.order.other;

import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IOrderSync56KService {

    /**
     * 获取56K唯一标识
     * @param orderId
     * @return
     * @throws Exception
     */
    String getTag56K(Long orderId) throws Exception;

    /**
     * 同步订单56K
     * @param orderId
     * @param isUpdate
     * @throws Exception
     */
    void syncOrderInfoTo56K(Long orderId, boolean isUpdate,boolean isDirectSync) ;

    /**
     * 删除56K订单
     * @param orderId
     * @throws Exception
     */
    void orderDelTo56K(Long orderId,boolean isDirectSync) throws Exception;

    /**
     * 同步订单轨迹
     * @param orderId
     * @throws Exception
     */
    void syncOrderTrackTo56K(Long orderId,boolean isDirectSync) throws Exception;

    /**
     * 同步订单轨迹时间
     * @param orderId
     * @param isDirectSync
     * @throws Exception
     */
    void syncOrderTrackDateTo56K(Long orderId,boolean isDirectSync)throws Exception;

    /**
     * 同步新增订单回调
     * @param inParam
     * @throws Exception
     */
    void syncOrderInfoCallBack(Map<String, Object> inParam) throws Exception;

    /**
     * 通用回调
     * @param inParam
     * @throws Exception
     */
    void syncCallBack(Map<String, Object> inParam) throws Exception;

    /**
     * 批量获取第三方ID
     * @param orderIds
     * @return
     * @throws Exception
     */
    Map<Long, String> getTag56KList(List<Long> orderIds)throws Exception;
    /**
     * 获取订单补充轨迹
     * @param orderScheduler
     * @param orderGoods
     * @param dependTime
     * @param arriveDate
     * @param isHis
     * @return
     * @throws Exception
     */
    List<Map> querySupplementOrderTrack(OrderScheduler orderScheduler, OrderGoods orderGoods, Date dependTime, Date arriveDate, Boolean isHis)throws Exception;

    /**
     * 查询订单同步轨迹时间
     * @param orderInfo
     * @param orderScheduler
     * @param orderInfoExt
     * @param isHis
     * @return
     * @throws Exception
     */
    Map<String,Date> queryOrderTrackDate(OrderInfo orderInfo, OrderScheduler orderScheduler, OrderInfoExt orderInfoExt, boolean isHis)throws Exception;

}
