package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderBillCheckInfoH;
import com.youming.youche.finance.dto.order.OrderCheckInfoDto;

import java.util.List;

public interface IOrderBillCheckInfoHService extends IBaseService<OrderBillCheckInfoH> {

    /**
     * 查询最近一次插入的账单的核销记录
     */
    List<OrderCheckInfoDto> queryRecentRecordByBillNumber(String billNumber);

}
