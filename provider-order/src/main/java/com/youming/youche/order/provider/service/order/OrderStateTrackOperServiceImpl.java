package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderStateTrackOperService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderStateTrackOper;
import com.youming.youche.order.provider.mapper.order.OrderStateTrackOperMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OrderStateTrackOperServiceImpl extends BaseServiceImpl<OrderStateTrackOperMapper, OrderStateTrackOper> implements IOrderStateTrackOperService {


    @Override
    public void saveOrUpdate(Long orderId, Long vehicleCode, Integer opType) {
        OrderStateTrackOper oper = this.getOrderStateTrackOperByOrderId(orderId);
        if (oper != null) {
            oper.setOpType(opType);
            oper.setTaskTrackSts(OrderConsts.TASK_TRACK_STS.NO_TRACK);
        }else{
            oper = new OrderStateTrackOper();
            oper.setOrderId(orderId);
            oper.setOpType(opType);
            oper.setVehicleCode(vehicleCode);
            oper.setTaskTrackSts(OrderConsts.TASK_TRACK_STS.NO_TRACK);
        }
        this.saveOrUpdate(oper);
    }

    @Override
    public OrderStateTrackOper getOrderStateTrackOperByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderStateTrackOper> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderStateTrackOper::getOrderId,orderId);
        List<OrderStateTrackOper> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }
}
