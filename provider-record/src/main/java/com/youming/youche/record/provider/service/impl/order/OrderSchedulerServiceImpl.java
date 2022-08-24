package com.youming.youche.record.provider.service.impl.order;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.record.api.order.IOrderSchedulerService;
import com.youming.youche.record.common.OrderConsts;
import com.youming.youche.record.domain.order.OrderScheduler;
import com.youming.youche.record.dto.order.OrderSchedulerDto;
import com.youming.youche.record.provider.mapper.order.OrderSchedulerMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单调度表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-23
 */
@DubboService(version = "1.0.0")
public class OrderSchedulerServiceImpl extends ServiceImpl<OrderSchedulerMapper, OrderScheduler>
        implements IOrderSchedulerService {

    @Autowired
    OrderSchedulerMapper orderSchedulerMapper;

    @Override
    public List<Map> queryDriverOrderInfo(Long userId, Long tenantId, String startDate, String endDate) {
        List<Map> listOut = new ArrayList<Map>();
        List<Map> orderList = orderSchedulerMapper.queryOrderInfo(userId, tenantId, startDate, endDate, OrderConsts.ORDER_STATE.CANCELLED);
        if (orderList != null && orderList.size() > 0) {
            listOut.addAll(orderList);
        }
        List<Map> orderHList = orderSchedulerMapper.queryOrderInfoH(userId, tenantId, startDate, endDate, OrderConsts.ORDER_STATE.CANCELLED);
        if (orderHList != null && orderHList.size() > 0) {
            listOut.addAll(orderHList);
        }
        return listOut;
    }

    @Override
    public boolean hasDriverOrder(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空！");
        }
        List<Map> orderList = this.queryDriverOrderInfo(userId, null, null, null);
        boolean isHasOrder = false;
        if (orderList != null && orderList.size() > 0) {
            for (Map map : orderList) {
                Integer vehicleClass = DataFormat.getIntKey(map, "vehicleClass");
                if (vehicleClass != 1) {
                    isHasOrder = true;
                    break;
                }
            }
        }
        return isHasOrder;
    }

    @Override
    public OrderScheduler getOrderSchedulerByOrderId(Long orderId) throws Exception {

        QueryWrapper<OrderScheduler> orderSchedulerQueryWrapper = new QueryWrapper<>();
        orderSchedulerQueryWrapper.eq("order_id", orderId);
        List<OrderScheduler> orderSchedulers = orderSchedulerMapper.selectList(orderSchedulerQueryWrapper);
        if (orderSchedulers != null && orderSchedulers.size() > 0) {
            return orderSchedulers.get(0);
        }
        return null;
    }

    @Override
    public OrderScheduler getOrderScheduler(Long orderId) {
        return baseMapper.getOrderScheduler(orderId);
    }

    @Override
    public Integer queryOrderdriverInfo(Long userId, Integer orderState) {
        return orderSchedulerMapper.queryOrderdriverInfo(userId,orderState);
    }

    @Override
    public List<OrderSchedulerDto> queryOrderInfoByCar(Long vehicleCode, Long tenantId, Integer fromOrderId, Integer fromTenantId,Long orderId,String plateNumber) {
        Integer orderState =  com.youming.youche.order.commons.OrderConsts.ORDER_STATE.CANCELLED;
        List<OrderSchedulerDto> list = new ArrayList<>();
        List<OrderSchedulerDto> orderSchedulerDtos = baseMapper.queryOrderInfoByCar(vehicleCode, tenantId, fromOrderId, fromTenantId,orderState,orderId,plateNumber);
        if (orderSchedulerDtos!=null && orderSchedulerDtos.size()>0){
            for (OrderSchedulerDto dto :orderSchedulerDtos) {
                list.add(dto);
            }
        }else {
            List<OrderSchedulerDto> orderSchedulerDtoLists = baseMapper.queryOrderInfoByCarH(vehicleCode, tenantId, fromOrderId, fromTenantId,orderState,orderId,plateNumber);
            for (OrderSchedulerDto dto :orderSchedulerDtoLists) {
                list.add(dto);
            }
        }
        return list;
    }
}

