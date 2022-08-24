package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户资料信息 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@RestController
@RequestMapping("/facilitator/user-data-info")
public class UserDataInfoController extends BaseController<UserDataInfo, IUserDataInfoMarketService> {
    @DubboReference(version = "1.0.0")
    IUserDataInfoMarketService userDataInfoService;

    @Override
    public IUserDataInfoMarketService getService() {
        return userDataInfoService;
    }

    /**
     * 注册用户总数
     */
    @GetMapping("getall")
    public Integer getAllUser() {

        return userDataInfoService.count();
    }

}
