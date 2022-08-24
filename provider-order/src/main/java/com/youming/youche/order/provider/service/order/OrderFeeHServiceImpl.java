package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.provider.mapper.order.OrderFeeHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 订单费用历史表 服务实现类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-21
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFeeHServiceImpl extends BaseServiceImpl<OrderFeeHMapper, OrderFeeH> implements IOrderFeeHService {


    @Override
    public OrderFeeH selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderFeeH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderFeeH::getOrderId,orderId);
        return getOne(wrapper,false);
    }

    @Override
    public OrderFeeH getOrderFeeH(Long orderId) {
        LambdaQueryWrapper<OrderFeeH> orderFeeHLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderFeeHLambdaQueryWrapper.eq(OrderFeeH::getOrderId, orderId);

        List<OrderFeeH> list = this.list(orderFeeHLambdaQueryWrapper);
        if (list != null && list.size() >0) {
            return list.get(0);
        }
        return null;
    }
}
