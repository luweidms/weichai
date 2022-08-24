package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IAccountBankUserTypeRelService;
import com.youming.youche.order.domain.order.AccountBankUserTypeRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 银行卡跟用户类型的关系表 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("account-bank-user-type-rel")
public class AccountBankUserTypeRelController extends BaseController<AccountBankUserTypeRel, IAccountBankUserTypeRelService> {
    @DubboReference(version = "1.0.0")
    IAccountBankUserTypeRelService accountBankUserTypeRelService;
    @Override
    public IAccountBankUserTypeRelService getService() {
        return accountBankUserTypeRelService;
    }
}
