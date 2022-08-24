package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOilRecord56kService;
import com.youming.youche.order.domain.order.OilRecord56k;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("oil-record56-k")
public class OilRecord56kController extends BaseController<OilRecord56k, IOilRecord56kService> {
    @DubboReference(version = "1.0.0")
    IOilRecord56kService oilRecord56kService;
    @Override
    public IOilRecord56kService getService() {
        return oilRecord56kService;
    }
}
