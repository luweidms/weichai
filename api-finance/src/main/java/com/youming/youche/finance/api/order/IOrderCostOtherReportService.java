package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
public interface IOrderCostOtherReportService extends IBaseService<OrderCostOtherReport> {

    /**
     * 根据费用上报id查询其他上报费用
     * @param relId
     * @param
     * @return
     * @throws Exception
     */
    List<OrderCostOtherReport> getOrderCostOtherReport(long relId);

    List<OrderCostOtherReport> selectFee(Long id,Long tenantId,String beginDate, String endDate);
}
