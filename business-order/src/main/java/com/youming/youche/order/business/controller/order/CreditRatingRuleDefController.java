package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.ICreditRatingRuleDefService;
import com.youming.youche.order.domain.order.CreditRatingRuleDef;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-04-21
 */
@RestController
@RequestMapping("credit-rating-rule-def")
public class CreditRatingRuleDefController extends BaseController<CreditRatingRuleDef, ICreditRatingRuleDefService> {
    @DubboReference(version = "1.0.0")
    ICreditRatingRuleDefService creditRatingRuleDefService;
    @Override
    public ICreditRatingRuleDefService getService() {
        return creditRatingRuleDefService;
    }
}
