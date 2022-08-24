package com.youming.youche.system.business.controller.tenant;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 访客档案 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-18
 */
@RestController
@RequestMapping("/sys-tenant-visit")
public class SysTenantVisitController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
