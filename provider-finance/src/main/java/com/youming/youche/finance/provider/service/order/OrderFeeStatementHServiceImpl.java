package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderFeeStatementHService;
import com.youming.youche.finance.api.order.IOrderFeeStatementService;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.finance.domain.order.OrderFeeStatementH;
import com.youming.youche.finance.provider.mapper.order.OrderFeeStatementHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * 订单账单信息历史表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-06
 */
@DubboService(version = "1.0.0")
public class OrderFeeStatementHServiceImpl extends BaseServiceImpl<OrderFeeStatementHMapper, OrderFeeStatementH> implements IOrderFeeStatementHService {

    @Lazy
    @Autowired
    IOrderFeeStatementService orderFeeStatementService;

    @Override
    public List<OrderFeeStatementH> getOrderFreeStatementHListByOrderIdList(List<Long> orderIdList) {
        LambdaQueryWrapper<OrderFeeStatementH> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrderFeeStatementH::getOrderId, orderIdList);
        return this.list(queryWrapper);
    }

    @Override
    public List<OrderFeeStatementH> queryOrderFeeStatementHByBillNumber(String billNumber) {
        LambdaQueryWrapper<OrderFeeStatementH> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderFeeStatementH::getBillNumber, billNumber);
        return this.list(queryWrapper);
    }

    @Override
    public String judgeDoesItExistOrderId(String orderId) {
        List<OrderFeeStatement> orderFreeStatementListByOrderIdList = orderFeeStatementService.getOrderFreeStatementListByOrderIdList(Arrays.asList(Long.parseLong(orderId)));
        List<OrderFeeStatementH> orderFreeStatementHListByOrderIdList = this.getOrderFreeStatementHListByOrderIdList(Arrays.asList(Long.parseLong(orderId)));
        String billNumber = "";
        if (orderFreeStatementListByOrderIdList != null && orderFreeStatementListByOrderIdList.size() != 0) {
            billNumber = orderFreeStatementListByOrderIdList.get(0).getBillNumber();
        }
        if (orderFreeStatementHListByOrderIdList != null && orderFreeStatementHListByOrderIdList.size() != 0) {
            billNumber = orderFreeStatementHListByOrderIdList.get(0).getBillNumber();
        }

        return billNumber;
    }


}
