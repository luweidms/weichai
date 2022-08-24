package com.youming.youche.finance.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.api.order.IOrderCostDetailReportService;
import com.youming.youche.finance.domain.order.OrderCostDetailReport;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
@RestController
@RequestMapping("order-cost-detail-report")
public class OrderCostDetailReportController extends BaseController<OrderCostDetailReport,IOrderCostDetailReportService> {


    @DubboReference(version = "1.0.0" )
    IOrderCostDetailReportService iOrderCostDetailReportService;

    @Override
    public IOrderCostDetailReportService getService() {
        return iOrderCostDetailReportService;
    }
}
