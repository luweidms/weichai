package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.api.order.IOrderDriverSubsidyVerService;
import com.youming.youche.order.domain.order.OrderDriverSubsidyVer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-23
*/
@RestController
@RequestMapping("order/driver/subsidy/ver")
public class OrderDriverSubsidyVerController extends BaseController<OrderDriverSubsidyVer, IOrderDriverSubsidyVerService> {
    @DubboReference(version = "1.0.0")
    IOrderDriverSubsidyVerService orderDriverSubsidyVerService;
    @Override
    public IOrderDriverSubsidyVerService getService() {
        return orderDriverSubsidyVerService;
    }
}
