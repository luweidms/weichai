package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.IUserDataInfoVerService;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@RestController
@RequestMapping("/facilitator/user-data-info-ver")
public class UserDataInfoVerController extends BaseController<UserDataInfoVer, IUserDataInfoVerService> {
    @DubboReference(version = "1.0.0")
    private IUserDataInfoVerService userDataInfoVerService;

    @Override
    public IUserDataInfoVerService getService() {
        return userDataInfoVerService;
    }
}
