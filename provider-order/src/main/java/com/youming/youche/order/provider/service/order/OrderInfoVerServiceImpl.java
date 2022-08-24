package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderInfoVerService;
import com.youming.youche.order.domain.order.OrderInfoVer;
import com.youming.youche.order.provider.mapper.order.OrderInfoVerMapper;
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
public class OrderInfoVerServiceImpl extends BaseServiceImpl<OrderInfoVerMapper, OrderInfoVer> implements IOrderInfoVerService {


    @Override
    public OrderInfoVer getOrderInfoVer(Long orderId) {
        LambdaQueryWrapper<OrderInfoVer> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderInfoVer::getOrderId,orderId).orderByDesc(OrderInfoVer::getId);
        List<OrderInfoVer> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }
}
