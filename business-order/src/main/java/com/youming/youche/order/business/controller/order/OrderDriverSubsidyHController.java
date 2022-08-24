package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.IOrderDriverSubsidyHService;
import com.youming.youche.order.domain.order.OrderDriverSubsidyH;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 *  司机补贴-历史表
 * </p>
 * @author liangyan
 * @since 2022-03-24
 */
@RestController
@RequestMapping("order-driver-subsidy-h")
public class OrderDriverSubsidyHController extends BaseController<OrderDriverSubsidyH, IOrderDriverSubsidyHService> {

    @DubboReference(version = "1.0.0")
    IOrderDriverSubsidyHService iOrderDriverSubsidyHService;
    @Override
    public IOrderDriverSubsidyHService getService() {
        return iOrderDriverSubsidyHService;
    }
}
