package com.youming.youche.finance.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderBillCheckInfoHService;
import com.youming.youche.finance.domain.order.OrderBillCheckInfoH;
import com.youming.youche.finance.dto.order.OrderCheckInfoDto;
import com.youming.youche.finance.provider.mapper.order.OrderBillCheckInfoHMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

@DubboService(version = "1.0.0")
public class OrderBillCheckInfoHServiceImpl extends BaseServiceImpl<OrderBillCheckInfoHMapper, OrderBillCheckInfoH> implements IOrderBillCheckInfoHService {

    @Override
    public List<OrderCheckInfoDto> queryRecentRecordByBillNumber(String billNumber) {
        List<OrderCheckInfoDto> list = baseMapper.queryRecentRecordByBillNumber(billNumber);
        if (list != null && list.size() > 0) {
            return list;
        }
        return new ArrayList<OrderCheckInfoDto>();
    }

}
