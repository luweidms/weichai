package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.oil.CarLastOil;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.dto.order.QueryOrderOilCardInfoDto;

import java.util.List;

/**
 * <p>
 * 订单油卡表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
public interface IOrderOilCardInfoService extends IBaseService<OrderOilCardInfo> {
    /**
     * 删除订单油卡
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    String deleteOrderOilCardInfo(Long orderId);

    /**
     * 查询订单油卡
     *
     * @param orderId
     * @param oilCardNum
     * @return
     * @throws Exception
     */
    List<OrderOilCardInfo> queryOrderOilCardInfoByOrderId(long orderId, String oilCardNum);

    /**
     * 查询订单油卡
     *
     * @param orderId
     * @param oilCardNum
     * @return
     * @throws Exception
     */
    List<QueryOrderOilCardInfoDto> queryOrderOilCardInfoByOrderIdWx(long orderId, String oilCardNum);

    /**
     * 根据车牌号查找该车辆最后一次绑定的车辆
     */
    CarLastOil getOilCarNumberByPlateNumber(String plateNumber, String accessToken);

    /**
     * 根据车牌号查找该车辆最后一次绑定的车辆
     */
    List<QueryOrderOilCardInfoDto> getOilCarNumberByPlateNumberWX(String plateNumber, String accessToken);

    /**
     * 获取预付款时订单输入的油卡和车辆绑定的油卡
     * @param orderId
     * @param accessToken
     * @return
     */
    List<String> queryOrdercConsumeCard(Long orderId,String accessToken);


    /**
     * 根据订单号查询订单油卡
     * @param orderId 订单ID
     * @param oilCardNum 油卡号
     * @return
     * @throws Exception
     */

    List<OrderOilCardInfo> queryOrderOilCardInfoByOrderIds(long orderId,String oilCardNum, String accessToken);
}
