package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.IOrderReportService;
import com.youming.youche.order.domain.order.OrderReport;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wuhao
 * @since 2022-03-29
 */
@RestController
@RequestMapping("order-report")
public class OrderReportController extends BaseController<OrderReport, IOrderReportService> {
    @DubboReference(version = "1.0.0")
    private  IOrderReportService iOrderReportService;
    @Override
    public IOrderReportService getService() {
        return iOrderReportService;
    }
}
