package com.youming.youche.capital.business.controller;


import com.youming.youche.capital.api.IPayFeeLimitVerService;
import com.youming.youche.capital.domain.PayFeeLimitVer;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import com.youming.youche.commons.base.BaseController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-03-03
 */
@RestController
@RequestMapping("pay/fee/limit/ver")
public class PayFeeLimitVerController extends BaseController {

    @DubboReference(version = "1.0.0")
    IPayFeeLimitVerService payFeeLimitVerService;

    @Override
    public IBaseService getService() {
        return payFeeLimitVerService;
    }

    /**
     * 查询审批失败原因
     * @return
     */
    @RequestMapping("/queryCheckFailMsg")
    public ResponseResult queryCheckFailMsg() throws InvocationTargetException, IllegalAccessException {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(payFeeLimitVerService.queryCheckFailMsg());
    }

    /**
     * 查询资金风控版本信息
     */
    @RequestMapping("/queryPayFeeLimitCfgUpdt")
    public ResponseResult queryPayFeeLimitCfgUpdt(@RequestParam String codeType,@RequestParam String codeDesc) throws InvocationTargetException, IllegalAccessException {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(payFeeLimitVerService.queryPayFeeLimitCfgUpdt(accessToken,codeType,codeDesc));
    }

    /**
     * 保存资金风控配置信息
     * @return
     * @throws Exception
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody List<PayFeeLimitVer> payFeeLimitVerList) throws Exception {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(payFeeLimitVerService.saveOrUpdate(accessToken,payFeeLimitVerList));
    }
}
