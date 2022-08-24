package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.dto.AppAccountDetailsDto;
import com.youming.youche.order.vo.AppAccountDetailsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/account/details")
public class AccountDetailsController extends BaseController<AccountDetails, IAccountDetailsService> {
    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;
    @Override
    public IAccountDetailsService getService() {
        return accountDetailsService;
    }


    /**
     * 收支明细(21114)
     * @param appAccountDetailsVo
     * @return
     */
    @GetMapping("getAccountDetail")
    public ResponseResult getAccountDetail(AppAccountDetailsVo appAccountDetailsVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        AppAccountDetailsDto accountDetail = accountDetailsService.getAccountDetail(appAccountDetailsVo,accessToken);
        return ResponseResult.success(accountDetail);
    }

    /**
     * 接口编码:21001
     * 我的--收支明细
     * 接口入参：
     * param month  查询年月
     * param type   0. 全部 1. 支出、2. 收入
     * param userId 用户id
     */
    @GetMapping("getAccountDetailNew")
    public ResponseResult getAccountDetailNew(AppAccountDetailsVo appAccountDetailsVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        AppAccountDetailsDto accountDetail = accountDetailsService.getAccountDetailNew(appAccountDetailsVo, accessToken);
        return ResponseResult.success(accountDetail);

    }

}
