package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.domain.order.RechargeOilSource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 充值油来源关系表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("recharge-oil-source")
public class RechargeOilSourceController extends BaseController<RechargeOilSource, IRechargeOilSourceService> {
    @DubboReference(version = "1.0.0")
    IRechargeOilSourceService rechargeOilSourceService;
    @Override
    public IRechargeOilSourceService getService() {
        return rechargeOilSourceService;
    }
}
