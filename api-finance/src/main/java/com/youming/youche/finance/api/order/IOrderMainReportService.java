package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderMainReport;

/**
 * <p>
 * 服务类
 * </p>
 *聂杰伟
 *订单成本上报主表
 * @author Terry
 * @since 2022-03-08
 */
public interface IOrderMainReportService extends IBaseService<OrderMainReport> {

    /***
     * 查询上报费用待审数量
     */
    Long getOrderCostReportAuditCount(String accessToken);

}
