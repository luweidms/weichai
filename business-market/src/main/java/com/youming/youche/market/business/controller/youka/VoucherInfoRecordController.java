package com.youming.youche.market.business.controller.youka;


import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 油卡充值代金券使用记录表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-03-08
 */
@RestController
@RequestMapping("voucher-info-record")
public class VoucherInfoRecordController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
