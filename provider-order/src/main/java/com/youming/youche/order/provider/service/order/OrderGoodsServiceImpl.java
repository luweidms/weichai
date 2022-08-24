package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.provider.mapper.order.OrderGoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * <p>
 * 订单货物表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
@Service
public class OrderGoodsServiceImpl extends BaseServiceImpl<OrderGoodsMapper, OrderGoods> implements IOrderGoodsService {


    @Override
    public OrderGoods getOrderGoods(Long orderId) {
        LambdaQueryWrapper<OrderGoods> lambda = new QueryWrapper<OrderGoods>().lambda();
        lambda.eq(OrderGoods::getOrderId, orderId);
        List<OrderGoods> list = this.list(lambda);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return new OrderGoods();
    }

    @Override
    public OrderGoods getCustomNumber(Long orderId) {
        LambdaQueryWrapper<OrderGoods> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderGoods::getCustomNumber, String.valueOf(orderId));
        List<OrderGoods> list = this.list(wrapper);
        if(list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public OrderGoods selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderGoods> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderGoods::getOrderId, orderId);
        return getOne(wrapper, false);
    }

    @Override
    public OrderGoods getOneGoodInfo(Long orderId) {

        LambdaQueryWrapper<OrderGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderGoods::getOrderId, orderId);

        List<OrderGoods> orderGoods = getBaseMapper().selectList(wrapper);
        if (orderGoods != null && orderGoods.size() == 1) {
            return orderGoods.get(0);
        }
        return null;
    }

    @Override
    public Set<OrderGoods> getOrderGoodsSet(Long orderId) {

        LambdaQueryWrapper<OrderGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderGoods::getOrderId, orderId);

        List<OrderGoods> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return new HashSet(list);
        } else {
            return null;
        }
    }

}

