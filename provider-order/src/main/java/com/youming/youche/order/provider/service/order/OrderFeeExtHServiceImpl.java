package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.provider.mapper.order.OrderFeeExtHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFeeExtHServiceImpl extends BaseServiceImpl<OrderFeeExtHMapper, OrderFeeExtH> implements IOrderFeeExtHService {


    @Override
    public OrderFeeExtH getOrderFeeExtH(Long orderId) {
        LambdaQueryWrapper<OrderFeeExtH> lambda=new QueryWrapper<OrderFeeExtH>().lambda();
        lambda.eq(OrderFeeExtH::getOrderId,orderId);
        List<OrderFeeExtH> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public OrderFeeExtH selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderFeeExtH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderFeeExtH::getOrderId,orderId);
        return getOne(wrapper,false);
    }
}
