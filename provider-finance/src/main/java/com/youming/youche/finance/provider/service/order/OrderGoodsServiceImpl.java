package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderGoodsService;
import com.youming.youche.finance.domain.order.OrderGoods;
import com.youming.youche.finance.provider.mapper.order.OrderGoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    * 订单货物表 服务实现类
    * </p>
* @author liangyan
* @since 2022-03-15
*/
@DubboService(version = "1.0.0")
public class OrderGoodsServiceImpl extends BaseServiceImpl<OrderGoodsMapper, OrderGoods> implements IOrderGoodsService {

@Resource
private OrderGoodsMapper orderGoodsMapper;
    @Override
    public OrderGoods getOrderGoodsByOrderId(Long orderId) throws Exception {

        QueryWrapper<OrderGoods> orderGoodsQueryWrapper = new QueryWrapper<>();
        orderGoodsQueryWrapper.eq("order_id",orderId);
        List<OrderGoods> orderGoods = orderGoodsMapper.selectList(orderGoodsQueryWrapper);
        if(orderGoods != null && orderGoods.size() > 0){
            return orderGoods.get(0);
        }
        return null;
    }
}
