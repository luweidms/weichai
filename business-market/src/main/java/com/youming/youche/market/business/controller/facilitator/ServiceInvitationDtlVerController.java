package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IServiceInvitationDtlVerService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务商申请合作明细版本 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@RestController
@RequestMapping("/facilitator/service-invitation-dtl-ver")
public class ServiceInvitationDtlVerController {
    @DubboReference(version = "1.0.0")
    IServiceInvitationDtlVerService serviceInvitationDtlVerService;

    public IServiceInvitationDtlVerService getService() {
        return serviceInvitationDtlVerService;
    }
}
