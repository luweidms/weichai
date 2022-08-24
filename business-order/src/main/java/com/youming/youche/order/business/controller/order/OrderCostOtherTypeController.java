package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xxx
 * @since 2022-03-29
 */
@RestController
@RequestMapping("order-cost-other-type")
public class OrderCostOtherTypeController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
