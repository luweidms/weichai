package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IWorkOrderRelService;
import com.youming.youche.order.domain.order.WorkOrderRel;
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
 * @since 2022-03-20
 */
@RestController
@RequestMapping("work-order-rel")
public class WorkOrderRelController extends BaseController<WorkOrderRel, IWorkOrderRelService> {
    @DubboReference(version = "1.0.0")
    IWorkOrderRelService workOrderRelService;
    @Override
    public IWorkOrderRelService getService() {
        return workOrderRelService;
    }
}
