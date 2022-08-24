package com.youming.youche.order.business.controller;


import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.domain.PayManager;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
* <p>
*  前端控制器
* </p>
* @author caoyajie
* @since 2022-04-15
*/
    @RestController
@RequestMapping("pay-manager")
        public class PayManagerController extends BaseController<PayManager, IPayManagerService> {

    @DubboReference(version = "1.0.0")
    IPayManagerService iPayManagerService;
    @Override
    public IPayManagerService getService() {
        return iPayManagerService;
    }
}
