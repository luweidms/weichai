package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.ICmbBankAccountInfoService;
import com.youming.youche.market.domain.facilitator.CmbBankAccountInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 招行帐户信息表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
@RestController
@RequestMapping("/facilitator/cmb-bank-account-info")
public class CmbBankAccountInfoController extends BaseController<CmbBankAccountInfo, ICmbBankAccountInfoService> {
    @DubboReference(version = "1.0.0")
    ICmbBankAccountInfoService cmbBankAccountInfoService;
    @Override
    public ICmbBankAccountInfoService getService() {
        return cmbBankAccountInfoService;
    }
}
