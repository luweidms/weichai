package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlService;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务商申请合作明细表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@RestController
@RequestMapping("/facilitator/service-invitation-dtl")
public class ServiceInvitationDtlController extends BaseController<ServiceInvitationDtl, IServiceInvitationDtlService> {
    @DubboReference(version = "1.0.0")
    IServiceInvitationDtlService serviceInvitationDtlService;
    @Override
    public IServiceInvitationDtlService getService() {
        return serviceInvitationDtlService;
    }
}
