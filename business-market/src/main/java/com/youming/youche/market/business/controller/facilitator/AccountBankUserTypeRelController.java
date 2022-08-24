package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.market.api.facilitator.IAccountBankUserTypeRelService;
import com.youming.youche.market.api.facilitator.ICmbBankAccountInfoService;
import com.youming.youche.market.domain.facilitator.AccountBankUserTypeRel;
import com.youming.youche.market.domain.facilitator.CmbBankAccountInfo;
import com.youming.youche.market.dto.user.AccountBankByTenantIdDto;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankByTenantIdVO;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankUserTypeRelVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 银行卡跟用户类型的关系表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
@RestController
@RequestMapping("/facilitator/account-bank-user-type-rel")
public class AccountBankUserTypeRelController extends BaseController<AccountBankUserTypeRel, IAccountBankUserTypeRelService> {
    @DubboReference(version = "1.0.0")
    IAccountBankUserTypeRelService accountBankUserTypeRelService;
    @Override
    public IAccountBankUserTypeRelService getService() {
        return accountBankUserTypeRelService;
    }

    @DubboReference(version = "1.0.0")
    ICmbBankAccountInfoService cmbBankAccountInfoService;

    /**
     * 录入订单页面  通过承运车队的tenantId，查找车队的收款账号和收款人
     * liangyan
     * 2022-3-12 10:00
     * @param tenantId 租户id或者叫车队id
     * @return
     */
    @GetMapping("/getAccountBankByTenantId")
    public ResponseResult getAccountBankRel(@RequestParam("tenantId") Long tenantId){
        try{
            AccountBankByTenantIdDto cmbBankAccountInfo = cmbBankAccountInfoService.getCmbBankAccountInfo(tenantId);
            return ResponseResult.success(cmbBankAccountInfo);
        }catch (Exception e){
             return ResponseResult.failure("查询异常");
        }
    }
}
