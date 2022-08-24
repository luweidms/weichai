package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;

import java.util.List;

/**
 * @author hzx
 * @date 2022/4/15 10:22
 */
public interface IOrderBillCheckInfoService extends IBaseService<OrderBillCheckInfo> {

    //修改核销明细，类型为现金核销的金额
    List<OrderBillCheckInfo> queryInfoByBillNumber(String billNumber);

    /**
     * 查询除历史表外的核销记录（核销金额、核销时间）
     */
    List<OrderBillCheckInfo> queryInfoByBillNumberAndIdAsc(String billNumber);

}
