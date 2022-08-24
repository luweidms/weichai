package com.youming.youche.order.api.order.other;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.ServiceOrderInfo;
import com.youming.youche.order.dto.PayDetailsDto;
import com.youming.youche.order.dto.order.OilVehiclesAndTenantDto;
import com.youming.youche.order.dto.order.QueryServiceOrderInfoDetailsDto;
import com.youming.youche.order.dto.order.ServiceOrderInfoListDto;

import java.util.Date;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author wuhao
* @since 2022-05-18
*/
    public interface IServiceOrderInfoService extends IBaseService<ServiceOrderInfo> {

    /**
     * 保存修改服务商订单
     * @param info
     * @return
     * @throws Exception
     */
    Long saveOrUpdates(ServiceOrderInfo info,String accessToken);

    /**
     * 修改服务商订单状态
     * @param id
     * @param orderState
     * @throws Exception
     */
    void updateServiceOrderState(Long id, Integer orderState,String accessToken);


    /**
     * 同步订单金额
     * @param id
     * @param oilBalance
     * @param cashBalance
     * @throws Exception
     */
    void synServiceOrderBalance(Long id,Long oilBalance,Long cashBalance,String accessToken);

    /**
     * 查询扫码加油支付详情
     * 40038
     */
    PayDetailsDto zhaoYouPayDetails(String orderId, String accessToken);

    /**
     * 获取待处理订单
     */
    List<ServiceOrderInfo> queryServiceOrderDisposeWait(Long userId, String orderId, String plateNumber, Date startDate, Date endDate);

    /**
     * 接口编号：40039
     * 扫码加油获取司机相关车辆以及车辆归属车队
     */
    List<OilVehiclesAndTenantDto> getAddOilVehiclesAndTenant(String accessToken);

    /**
     * 加油记录(新)-- 40040
     */
    Page<ServiceOrderInfoListDto> queryServiceOrderInfoList(String accessToken,Integer pageNum,Integer pageSize);

    /**
     * 加油评价接口-App接口 40042
     */
    void evaluateServiceOrderInfo(Integer evaluateService, Integer evaluateQuality, Integer evaluatePrice, Long serviceOrderId, String accessToken);

    /**
     * 加油记录详情-App接口  40041
     */
    QueryServiceOrderInfoDetailsDto queryServiceOrderInfoDetails(Long serviceOrderId);

    Page<ServiceOrderInfo> queryServiceOrderList(Long userId, String plateNumber, Date startDate, Date endDate,Integer pageNum,Integer pageSize);

}
