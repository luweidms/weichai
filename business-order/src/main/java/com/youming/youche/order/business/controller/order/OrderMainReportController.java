package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.IOrderMainReportService;
import com.youming.youche.order.domain.order.OrderMainReport;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-22
*/
@RestController
@RequestMapping("order/main/report")
public class OrderMainReportController extends BaseController<OrderMainReport, IOrderMainReportService> {

    @DubboReference(version = "1.0.0")
    IOrderMainReportService orderMainReportService;
    @Override
    public IOrderMainReportService getService() {
        return orderMainReportService;
    }
}
