package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.domain.TenantServiceRel;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.domain.order.OilRechargeAccount;
import com.youming.youche.order.dto.OilRechargeAccountDto;
import com.youming.youche.order.dto.PingAnBalanceDto;
import com.youming.youche.order.dto.order.AccountBankRelDto;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsOutDto;
import com.youming.youche.order.vo.OilRechargeAccountVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("oil/recharge/account")
public class OilRechargeAccountController extends BaseController<OilRechargeAccount, IOilRechargeAccountService> {
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountService oilRechargeAccountService;
    @Override
    public IOilRechargeAccountService getService() {
        return oilRechargeAccountService;
    }

    /**
     * niejeiwei
     * 司机小程序
     * 客户油-小程序接口
     * 50050
     * @return
     */
    @GetMapping("/queryOilAccountDetails")
    public ResponseResult queryOilAccountDetails(@RequestParam(value = "userId") Long userId,
                                                 @RequestParam(value = "userType") Integer userType,
                                                 @RequestParam(value = "tenantName") String tenantName,
                                                 @RequestParam(value = "billId") String billId,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OilRechargeAccountDto> oilRechargeAccountDtoPage =
                oilRechargeAccountService.queryOilAccountDetails(userId, billId,
                tenantName, userType, pageNum, pageSize, accessToken);
        return  ResponseResult.success(oilRechargeAccountDtoPage);
    }

    /**
     * niejeiwei
     * 司机小程序
     * 授信列表-小程序接口
     * 50051
     * @return
     */
    @GetMapping("/queryCreditLineDetails")
    public ResponseResult queryCreditLineDetails(@RequestParam(value = "serviceName") String serviceName,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<TenantServiceRel> page = oilRechargeAccountService.queryCreditLineDetails(accessToken, serviceName, pageNum, pageSize);
        return  ResponseResult.success(page);
    }
    /**
     * niejeiwei
     * 司机小程序
     * 预存资金-小程序接口
     * 50052
     * @return
     */
    @GetMapping("/queryLockBalanceDetails")
    public ResponseResult queryLockBalanceDetails(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AccountBankRelDto> page = oilRechargeAccountService.queryLockBalanceDetails(null, accessToken, pageNum, pageSize);
        return  ResponseResult.success(page);
    }

    /**
     * niejiewei
     * 司机小程序
     * 充值账户-小程序接口
     * 50053
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getOilRechargeAccountDetails")
    public  ResponseResult getOilRechargeAccountDetails(@RequestParam(value = "type") Integer type,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OilRechargeAccountDetailsOutDto> oilRechargeAccountDetails = oilRechargeAccountService.getOilRechargeAccountDetails(accessToken, type, pageNum, pageSize);
        return  ResponseResult.success(oilRechargeAccountDetails);
    }

    /**
     *移除账户-小程序接口
     * niejiewei
     * 司机小程序
     * 50054
     * @param id
     * @return
     */
    @PostMapping("/removeRechargeAccount")
    public  ResponseResult   removeRechargeAccount(OilRechargeAccountVo vo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto dto = oilRechargeAccountService.removeRechargeAccount(vo.getId(), accessToken);
        return  ResponseResult.success(dto);
    }

    /**
     * 获取充值信息-小程序接口
     * niejiewei
     * 司机小程序
     * 50055
     * @return
     */
    @GetMapping("/getRechargeInfo")
    public  ResponseResult getRechargeInfo(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OilRechargeAccountDto rechargeInfo = oilRechargeAccountService.getRechargeInfo(null, accessToken);
        return  ResponseResult.success(rechargeInfo);
    }

    /**
     * 司机小程序
     * niejeiwei
     * 50056
     * 确认充值-小程序接口
     * @param vo
     * @return
     */
    @PostMapping("/confirmRecharge")
    public  ResponseResult   confirmRecharge(OilRechargeAccountVo vo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        oilRechargeAccountService.confirmRecharge(vo,accessToken);
        return  ResponseResult.success();
    }
}
