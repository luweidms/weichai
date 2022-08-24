package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderCostDetailReport;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *订单成本上报费用明细表
 * @author Terry
 * @since 2022-03-09
 */
public interface IOrderCostDetailReportService extends IBaseService<OrderCostDetailReport> {



    /**
     * 通过订单号，获取上报费用明细记录
     * @param orderId
     * @return
     * @throws Exception
     */
    List<OrderCostDetailReport> getOrderCostDetailReportByOrderId(Long orderId, String accessToken);

}
