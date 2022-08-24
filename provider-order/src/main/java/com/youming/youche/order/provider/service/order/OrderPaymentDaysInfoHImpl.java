package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoHService;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoH;
import com.youming.youche.order.provider.mapper.order.OrderPaymentDaysInfoHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author hzx
 * @date 2022/3/23 17:44
 */
@DubboService(version = "1.0.0")
@Service
public class OrderPaymentDaysInfoHImpl extends BaseServiceImpl<OrderPaymentDaysInfoHMapper, OrderPaymentDaysInfoH> implements IOrderPaymentDaysInfoHService {
    @Override
    public OrderPaymentDaysInfoH queryOrderPaymentDaysInfoH(Long orderId, Integer paymentDaysType) {
        LambdaQueryWrapper<OrderPaymentDaysInfoH> queryWrapper = new LambdaQueryWrapper<>();
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空！请联系客服！");
        }
        if (paymentDaysType == null || paymentDaysType <= 0) {
            throw new BusinessException("账期类型为空！请联系客服！");
        }
        queryWrapper.eq(OrderPaymentDaysInfoH::getOrderId, orderId);
        queryWrapper.eq(OrderPaymentDaysInfoH::getPaymentDaysType, paymentDaysType);
        return this.getOne(queryWrapper);
    }
}
