package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.IAccountBankRelService;
import com.youming.youche.market.domain.facilitator.AccountBankRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
@RestController
@RequestMapping("/facilitator/account-bank-rel")
public class AccountBankRelController extends BaseController<AccountBankRel, IAccountBankRelService> {
    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;
    @Override
    public IAccountBankRelService getService() {
        return accountBankRelService;
    }
}
