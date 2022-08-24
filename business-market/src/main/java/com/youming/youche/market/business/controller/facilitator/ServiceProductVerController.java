package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IServiceProductVerService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务商站点版本表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@RestController
@RequestMapping("/facilitator/service-product-ver")
public class ServiceProductVerController  {
    @DubboReference(version = "1.0.0")
    private IServiceProductVerService serviceProductVerService;


    public IServiceProductVerService getService() {
        return serviceProductVerService;
    }
}
