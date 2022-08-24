package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.ICreditRatingRuleDefService;
import com.youming.youche.order.domain.order.CreditRatingRuleDef;
import com.youming.youche.order.provider.mapper.order.CreditRatingRuleDefMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-04-21
 */
@DubboService(version = "1.0.0")
@Service
public class CreditRatingRuleDefServiceImpl extends BaseServiceImpl<CreditRatingRuleDefMapper, CreditRatingRuleDef> implements ICreditRatingRuleDefService {


}
