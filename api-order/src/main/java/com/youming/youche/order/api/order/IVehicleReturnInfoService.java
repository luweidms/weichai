package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.VehicleReturnInfo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
public interface IVehicleReturnInfoService extends IBaseService<VehicleReturnInfo> {

    /**
     * 批量失效回货信息
     * @param plateNumber 车牌号
     * @param inValidDate 失效日期
     * @param isReturn 是否回货
     * @throws Exception
     */
     void disableVehicleReturns(String plateNumber, String inValidDate, Boolean isReturn);



    /**
     * 新增回货信息
     * @param returnInfo
     */
     void addVehicleReturn(VehicleReturnInfo returnInfo, LoginInfo user);


    /**
     * 查询车辆最近到达时间
     * @param plateNumber 车牌号
     * @param orderId 订单号
     * @return
     * relDate 到达时间
     * @throws Exception
     */
    String queryRecentArriveDate(String plateNumber, Long orderId);

    /**
     * 新增回货信息
     * @param reutrnInfo
     */
    void addVehicleReturn(VehicleReturnInfo reutrnInfo);
}
