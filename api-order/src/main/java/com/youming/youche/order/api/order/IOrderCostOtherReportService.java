package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderCostOtherReport;
import com.youming.youche.order.vo.OrderCostOtherReportVO;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author xxx
* @since 2022-03-29
*/
    public interface IOrderCostOtherReportService extends IBaseService<OrderCostOtherReport> {

    /**
     * 根据费用上报id查询其他上报费用
     * @param relId
     * @param b
     * @return
     */
    List<OrderCostOtherReportVO> getOrderCostOtherReport(Long relId, boolean b);
}
