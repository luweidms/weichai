package com.youming.youche.market.business.controller.youka;


import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 油卡消费返利记录表 前端控制器
 * </p>
 *
 * @author XXX
 * @since 2022-03-23
 */
@RestController
@RequestMapping("recharge-consume-rebate")
public class RechargeConsumeRebateController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
