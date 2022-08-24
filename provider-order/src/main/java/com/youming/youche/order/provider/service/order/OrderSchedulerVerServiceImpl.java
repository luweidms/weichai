package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderSchedulerVerService;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
import com.youming.youche.order.domain.order.OrderSchedulerVer;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerVerMapper;
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
public class OrderSchedulerVerServiceImpl extends BaseServiceImpl<OrderSchedulerVerMapper, OrderSchedulerVer> implements IOrderSchedulerVerService {


    @Override
    public OrderSchedulerVer getOrderSchedulerVer(Long orderId) {
        LambdaQueryWrapper<OrderSchedulerVer> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderSchedulerVer::getOrderId,orderId).orderByDesc(OrderSchedulerVer::getId);
        List<OrderSchedulerVer> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }
}
