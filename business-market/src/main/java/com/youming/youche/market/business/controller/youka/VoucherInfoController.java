package com.youming.youche.market.business.controller.youka;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.api.youka.IVoucherInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 代金券信息表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-03-08
 */
@RestController
@RequestMapping("voucherInfo")
public class VoucherInfoController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(VoucherInfoController.class);
    @DubboReference(version = "1.0.0")
    private IVoucherInfoService iVoucherInfoService;


}
