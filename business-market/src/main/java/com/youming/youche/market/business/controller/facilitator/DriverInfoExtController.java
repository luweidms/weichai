package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.IDriverInfoExtService;
import com.youming.youche.market.domain.facilitator.DriverInfoExt;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 司机信息扩展表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-01
 */
@RestController
@RequestMapping("/facilitator/driver-info-ext")
public class DriverInfoExtController extends BaseController<DriverInfoExt, IDriverInfoExtService> {
    @DubboReference(version = "1.0.0")
    IDriverInfoExtService driverInfoExtService;
    @Override
    public IDriverInfoExtService getService() {
        return driverInfoExtService;
    }
}
