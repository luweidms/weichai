package com.youming.youche.record.business.controller.account;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.domain.account.AccountBankRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzx
 * @date 2022/4/21 19:26
 */
@RestController
@RequestMapping("account/bank/rel")
public class AccountBankRelController extends BaseController<AccountBankRel, IAccountBankRelService> {

    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;

    @Override
    public IAccountBankRelService getService() {
        return iAccountBankRelService;
    }

    /**
     * 接口编号 10037
     * 接口入参：
     * bankType        银行类型（0：对私，1：对公）（可选）
     * 接口出参：
     * acctName        账户名
     * acctNo          银行卡号
     * bankName        开户行
     * bankProvCity    开户省市
     * branchName      开户支行
     * 获取充值账户信息
     */
    @GetMapping("getPTAccount")
    public ResponseResult getPTAccount(Integer bankType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(iAccountBankRelService.getPTAccountAndTailNumber(bankType, accessToken));
    }

}
