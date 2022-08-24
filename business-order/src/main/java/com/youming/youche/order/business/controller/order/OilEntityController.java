package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOilEntityService;
import com.youming.youche.order.domain.order.OilEntity;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 油充值核销表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("oil-entity")
public class OilEntityController extends BaseController<OilEntity, IOilEntityService> {
    @DubboReference(version = "1.0.0")
    IOilEntityService oilEntityService;
    @Override
    public IOilEntityService getService() {
        return oilEntityService;
    }
}
