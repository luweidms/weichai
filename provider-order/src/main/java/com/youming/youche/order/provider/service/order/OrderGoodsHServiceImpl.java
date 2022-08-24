package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.provider.mapper.order.OrderGoodsHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 订单货物历史表 服务实现类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderGoodsHServiceImpl extends BaseServiceImpl<OrderGoodsHMapper, OrderGoodsH> implements IOrderGoodsHService {


    @Override
    public OrderGoodsH selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderGoodsH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderGoodsH::getOrderId,orderId);
        return getOne(wrapper,false);
    }

    @Override
    public OrderGoodsH getOrderGoodsH(Long orderId) {
        LambdaQueryWrapper<OrderGoodsH> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderGoodsH::getOrderId, orderId);

        List<OrderGoodsH> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public OrderGoodsH getCustomNumberH(Long orderId) {
        LambdaQueryWrapper<OrderGoodsH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderGoodsH::getCustomNumber, String.valueOf(orderId));
        List<OrderGoodsH> list = this.list(wrapper);
        if(list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }
/*
    //挂车基础表 start
    TrailerManagement getTrailerManagement(String trailerNumber, Long tenantId);

    *//**
     * 查询挂车信息
     *//*
    VehicleCertInfoDto getTenantTrialer(String plateNumber, Long tenantId);*/

    @Override
    public OrderGoodsH getOneGoodInfoH(Long orderId) {
        return selectByOrderId(orderId);
    }
}
