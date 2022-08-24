package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.order.provider.mapper.order.PayoutIntfExpansionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class PayoutIntfExpansionServiceImpl extends BaseServiceImpl<PayoutIntfExpansionMapper, PayoutIntfExpansion> implements IPayoutIntfExpansionService {


    @Override
    public PayoutIntfExpansion getPayoutIntfExpansion(Long flowId) {
        LambdaQueryWrapper<PayoutIntfExpansion> lambda= Wrappers.lambdaQuery();
        lambda.eq(PayoutIntfExpansion::getFlowId,flowId);
        lambda.last("limit 1");
        return this.getOne(lambda);
    }
}
