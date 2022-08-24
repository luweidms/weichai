package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IBillInfoReceiveRelService;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("bill/info/receive/rel")
public class BillInfoReceiveRelController {
    @DubboReference(version = "1.0.0")
    IBillInfoReceiveRelService billInfoReceiveRelService;

    public IBillInfoReceiveRelService getService() {
        return billInfoReceiveRelService;
    }

    @Resource
    private HttpServletRequest request;

    /**
     * WX接口-获取车队默认收票主体 30079
     */
    @GetMapping("getDefaultBillInfo")
    public ResponseResult getDefaultBillInfo() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BillInfoReceiveRel defaultBillInfo = billInfoReceiveRelService.getDefaultBillInfo(accessToken);
        return ResponseResult.success(defaultBillInfo);
    }
}
