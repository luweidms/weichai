package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IBillReceiveService;
import com.youming.youche.order.domain.order.BillReceive;
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
 * @since 2022-03-19
 */
@RestController
@RequestMapping("bill-receive")
public class BillReceiveController {
    @DubboReference(version = "1.0.0")
    IBillReceiveService billReceiveService;

    public IBillReceiveService getService() {
        return billReceiveService;
    }
}
