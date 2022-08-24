package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.IOaLoadVerificationService;
import com.youming.youche.finance.domain.OaLoadVerification;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
@RestController
@RequestMapping("oa/load/verification")
public class OaLoadVerificationController extends BaseController<OaLoadVerification, IOaLoadVerificationService> {

    @DubboReference(version = "1.0.0")
    IOaLoadVerificationService iOaLoadVerificationService;

    @Override
    public IOaLoadVerificationService getService() {
        return iOaLoadVerificationService;
    }
}
