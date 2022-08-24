package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.provider.mapper.order.OrderTransitLineInfoHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@DubboService(version = "1.0.0")
@Service
public class OrderTransitLineInfoHServiceImpl extends BaseServiceImpl<OrderTransitLineInfoHMapper, OrderTransitLineInfoH> implements IOrderTransitLineInfoHService {


    @Override
    public List<OrderTransitLineInfoH> queryOrderTransitLineInfoHByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderTransitLineInfoH> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderTransitLineInfoH::getOrderId,orderId)
              .orderByAsc(OrderTransitLineInfoH::getSortId);
        return this.list(lambda);
    }
}
