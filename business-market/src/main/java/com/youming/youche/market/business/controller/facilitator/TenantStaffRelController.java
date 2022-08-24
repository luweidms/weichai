package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.ITenantStaffRelService;
import com.youming.youche.market.domain.facilitator.TenantStaffRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-07
 */
@RestController
@RequestMapping("/facilitator/tenant-staff-rel")
public class TenantStaffRelController extends BaseController<TenantStaffRel, ITenantStaffRelService> {
    @DubboReference(version = "1.0.0")
    ITenantStaffRelService tenantStaffRelService;
    @Override
    public ITenantStaffRelService getService() {
        return tenantStaffRelService;
    }
}
