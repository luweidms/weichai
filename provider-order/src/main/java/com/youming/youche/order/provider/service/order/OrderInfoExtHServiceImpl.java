package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.provider.mapper.order.OrderInfoExtHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderInfoExtHServiceImpl extends BaseServiceImpl<OrderInfoExtHMapper, OrderInfoExtH> implements IOrderInfoExtHService {


    @Override
    public OrderInfoExtH selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderInfoExtH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderInfoExtH::getOrderId,orderId);
        return getOne(wrapper,false);

    }

    @Override
    public OrderInfoExtH getOrderInfoExtH(Long orderId) {

        LambdaQueryWrapper<OrderInfoExtH> orderInfoExtHLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoExtHLambdaQueryWrapper.eq(OrderInfoExtH::getOrderId, orderId);

        List<OrderInfoExtH> list = this.list(orderInfoExtHLambdaQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
