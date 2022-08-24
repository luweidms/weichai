package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderFeeStatementService;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.finance.provider.mapper.order.OrderFeeStatementMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 订单账单信息表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-06
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFeeStatementServiceImpl extends BaseServiceImpl<OrderFeeStatementMapper, OrderFeeStatement> implements IOrderFeeStatementService {

    @Override
    public List<OrderFeeStatement> getOrderFreeStatementListByOrderIdList(List<Long> orderIdList) {
        LambdaQueryWrapper<OrderFeeStatement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrderFeeStatement::getOrderId, orderIdList);
        return this.list(queryWrapper);
    }

    @Override
    public List<OrderFeeStatement> queryOrderFeeStatementByBillNumber(String billNumber) {
        LambdaQueryWrapper<OrderFeeStatement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderFeeStatement::getBillNumber, billNumber);
        return this.list(queryWrapper);
    }

    @Override
    public List<OrderFeeStatement> lists(Long id, Long tenantId, String beginDate, String endDate) {
        QueryWrapper<OrderFeeStatement> orderFeeStatementQueryWrapper = new QueryWrapper<>();
        orderFeeStatementQueryWrapper.eq("tenant_id",tenantId);
        orderFeeStatementQueryWrapper.between("create_time", beginDate, endDate);
        List<OrderFeeStatement> list = this.list(orderFeeStatementQueryWrapper);
        return list;
    }
}
