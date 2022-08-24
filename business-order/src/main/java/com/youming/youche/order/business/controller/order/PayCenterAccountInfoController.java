package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.other.IPayCenterAccountInfoService;
import com.youming.youche.order.domain.order.PayCenterAccountInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-29
*/
@RestController
@RequestMapping("pay/center/account/info")
public class PayCenterAccountInfoController extends BaseController<PayCenterAccountInfo, IPayCenterAccountInfoService> {
    @DubboReference(version = "1.0.0")
    IPayCenterAccountInfoService payCenterAccountInfoService;
    @Override
    public IPayCenterAccountInfoService getService() {
        return payCenterAccountInfoService;
    }
}
