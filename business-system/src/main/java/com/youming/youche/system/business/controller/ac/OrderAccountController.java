package com.youming.youche.system.business.controller.ac;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ac.IOrderAccountService;
import com.youming.youche.system.dto.ac.AccountDetailsWXDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 订单账户表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@RestController
@RequestMapping("/system/orderAccount")
public class OrderAccountController extends BaseController {

    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;


    @Override
    public IBaseService getService() {
        return orderAccountService;
    }

    /***
     * @Description: 查询用户订单账户
     * @Author: luwei
     * @Date: 2022/1/24 5:13 下午
     * @Param userId:
     * @Param tenantId:
     * @Param userType:
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping("getAccountByUserId")
    public ResponseResult getAccountByUserId(@RequestParam("userId") Long userId, @RequestParam("tenantId") Long tenantId, @RequestParam(value = "userType", required = false) Integer userType) {
        return ResponseResult.success(orderAccountService.getOrderAccount(userId, tenantId, userType));
    }

    @GetMapping("queryAccount")
    public ResponseResult queryAccount(@RequestParam("userId") Long userId, @RequestParam(value = "userType", required = false) Integer userType) {
//
//        IWithdrawalsTF iWithdrawalsTF = CallerProxy.getSVBean(IWithdrawalsTF.class, "withdrawalsTF");
//        return JsonHelper.json(iWithdrawalsTF.queryAccount(userId, userType));
        return ResponseResult.success();
    }

    @GetMapping("getAccountDetailsWX")
    public ResponseResult getAccountDetailsWX(String name) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        AccountDetailsWXDto accountDetailsWX = orderAccountService.getAccountDetailsWX(name, accessToken);
        return ResponseResult.success(accountDetailsWX);
    }

}
