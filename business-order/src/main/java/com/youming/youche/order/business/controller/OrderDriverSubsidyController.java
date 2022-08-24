package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-12
*/
@RestController
@RequestMapping("order-driver-subsidy")
public class OrderDriverSubsidyController extends BaseController<OrderDriverSubsidy, IOrderDriverSubsidyService> {

//    @Resource
//    private IOrderDriverSubsidyService orderDriverSubsidyService;
    @Override
    public IOrderDriverSubsidyService getService() {
        return null;
    }
}
