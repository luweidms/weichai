package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderReport;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.VehiclesDto;

import java.util.List;

/**
 * <p>
 * 服务类 订单报备表
 * </p>
 *
 * @author wuhao
 * @since 2022-03-29
 */
public interface IOrderReportService extends IBaseService<OrderReport> {


    /**
     * 查询订单报备
     * 聂杰伟
     */
    List<OrderReportDto> queryOrderReportList (Long orderId, Long tenantId, Long userId, String accessToken);

    /**
     * 查询司机在途订单中使用的油站
     *
     * @param userId
     *            司机ID
     * @param oilId
     *            油站ID
     * @return
     * @throws Exception
     */
    List<Long> queryOrderOilEnRouteUse(Long userId, Long oilId);

    /**
     * 查询订单报备
     */
    List<OrderReportDto> queryOrderReport (Long orderId, Long userId, String accessToken);

    /**
     * 40034
     * @param accessToken
     * @return
     */
    VehiclesDto getVehicle(String accessToken);
}
