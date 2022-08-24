package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.domain.order.PayoutOrder;
import com.youming.youche.order.provider.mapper.order.PayoutOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
public class PayoutOrderServiceImpl extends BaseServiceImpl<PayoutOrderMapper, PayoutOrder> implements IPayoutOrderService {


    @Override
    public PayoutOrder createPayoutOrder(Long userId, Long amount, Integer amountType, Long batchId, Long orderId, Long tenantId, String vehicleAffiliation) {
        PayoutOrder payoutOrder = new PayoutOrder();
        payoutOrder.setUserId(userId);
        payoutOrder.setAmount(amount);
        payoutOrder.setAmountType(amountType);
        payoutOrder.setBatchId(batchId);
        payoutOrder.setOrderId(orderId);
        payoutOrder.setTenantId(tenantId);
        payoutOrder.setVehicleAffiliation(vehicleAffiliation);
        payoutOrder.setCreateTime(LocalDateTime.now());
        this.save(payoutOrder);
        return payoutOrder;
    }

    @Override
    public void updPayoutOrder(Long batchId, Long flowId) {
        LambdaUpdateWrapper<PayoutOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayoutOrder::getBatchId, flowId);
        updateWrapper.set(PayoutOrder::getBatchId, batchId);
        this.update(updateWrapper);
    }

    @Override
    public List<PayoutOrder> getPayoutOrderList(List<Long> batchIds) {
        if (batchIds == null || batchIds.size() <= 0) {
            throw new BusinessException("没有查询参数！");
        }
        LambdaQueryWrapper<PayoutOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PayoutOrder::getBatchId, batchIds);
        return this.list(queryWrapper);
    }

}
