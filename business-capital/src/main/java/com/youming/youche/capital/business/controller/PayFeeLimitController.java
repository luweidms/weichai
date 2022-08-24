package com.youming.youche.capital.business.controller;


import com.youming.youche.capital.api.IPayFeeLimitService;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;


/**
* <p>
*  前端控制器
* </p>
* @author Terry
* @since 2022-03-02
*/
@RestController
@RequestMapping("pay/fee/limit")
public class PayFeeLimitController extends BaseController {

    @DubboReference(version = "1.0.0")
    IPayFeeLimitService iPayFeeLimitService;

    @Override
    public IBaseService getService() {
        return iPayFeeLimitService;
    }

    /**
     * 查询资金信息
     */
    @GetMapping("/queryPayFeeLimitCfg")
    public ResponseResult queryPayFeeLimitCfg(@RequestParam String codeType,@RequestParam String codeDesc) throws InvocationTargetException, IllegalAccessException {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(iPayFeeLimitService.queryPayFeeLimitCfg(accessToken,codeType,codeDesc));
    }

    /***
     * @Description: 根据订单id查询借支上线
     * @Author: luwei
     * @Date: 2022/7/7 14:02
     * @Param codeType:
      * @Param codeDesc:
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping("/queryPayFeeLimitCfgOrder")
    public ResponseResult queryPayFeeLimitCfgOrder(@RequestParam String codeType,@RequestParam String codeDesc,@RequestParam Long tenantId) {
        return ResponseResult.success(iPayFeeLimitService.queryPayFeeLimitCfgOrder(codeType,codeDesc,tenantId));
    }

}
