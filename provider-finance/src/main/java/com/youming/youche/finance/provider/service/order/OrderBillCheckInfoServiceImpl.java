package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderBillCheckInfoService;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import com.youming.youche.finance.provider.mapper.order.OrderBillCheckInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @author hzx
 * @date 2022/4/15 10:22
 */
@DubboService(version = "1.0.0")
public class OrderBillCheckInfoServiceImpl extends BaseServiceImpl<OrderBillCheckInfoMapper, OrderBillCheckInfo> implements IOrderBillCheckInfoService {
    @Override
    public List<OrderBillCheckInfo> queryInfoByBillNumber(String billNumber) {
        LambdaQueryWrapper<OrderBillCheckInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderBillCheckInfo::getBillNumber, billNumber);
        return this.list(queryWrapper);
    }

    @Override
    public List<OrderBillCheckInfo> queryInfoByBillNumberAndIdAsc(String billNumber) {
        LambdaQueryWrapper<OrderBillCheckInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderBillCheckInfo::getBillNumber, billNumber);

        queryWrapper.orderByAsc(OrderBillCheckInfo::getId);
        return this.list(queryWrapper);
    }

}
