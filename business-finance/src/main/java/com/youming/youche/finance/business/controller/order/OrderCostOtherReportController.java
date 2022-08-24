package com.youming.youche.finance.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.order.IOrderCostOtherReportService;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 *  订单费用上报其他项
 * </p>
 * @author Terry
 * @since 2022-03-09
 */
@RestController
@RequestMapping("order-cost-other-report")
public class OrderCostOtherReportController extends BaseController<OrderCostOtherReport, IOrderCostOtherReportService> {


    @DubboReference(version = "1.0.0")
    IOrderCostOtherReportService iOrderCostOtherReportService;
    @Override
    public IOrderCostOtherReportService getService() {
        return iOrderCostOtherReportService;
    }
}
