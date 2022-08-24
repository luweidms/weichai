package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IWorkOrderInfoService;
import com.youming.youche.order.domain.order.WorkOrderInfo;
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
@RequestMapping("work-order-info")
public class WorkOrderInfoController extends BaseController<WorkOrderInfo, IWorkOrderInfoService> {
    @DubboReference(version = "1.0.0")
    IWorkOrderInfoService workOrderInfoService;
    @Override
    public IWorkOrderInfoService getService() {
        return workOrderInfoService;
    }


}
