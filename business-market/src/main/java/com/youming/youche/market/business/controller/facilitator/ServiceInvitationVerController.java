package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IServiceInvitationVerService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务商申请合作 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@RestController
@RequestMapping("/facilitator/service-invitation-ver")
public class ServiceInvitationVerController {
    @DubboReference(version = "1.0.0")
    IServiceInvitationVerService serviceInvitationVerService;

    public IServiceInvitationVerService getService() {
        return serviceInvitationVerService;
    }
}
