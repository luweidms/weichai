package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IConsumeOilFlowExtService;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("consume-oil-flow-ext")
public class ConsumeOilFlowExtController extends BaseController<ConsumeOilFlowExt, IConsumeOilFlowExtService> {
    @DubboReference(version = "1.0.0")
    IConsumeOilFlowExtService consumeOilFlowExtService;
    @Override
    public IConsumeOilFlowExtService getService() {
        return consumeOilFlowExtService;
    }
}
