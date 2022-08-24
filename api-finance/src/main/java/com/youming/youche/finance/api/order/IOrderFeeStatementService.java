package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.record.domain.violation.ViolationRecord;

import java.util.List;

/**
 * <p>
 * 订单账单信息表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-06
 */
public interface IOrderFeeStatementService extends IBaseService<OrderFeeStatement> {

    /**
     * 获取所有订单的订单账单信息，按分摊是的顺序
     */
    List<OrderFeeStatement> getOrderFreeStatementListByOrderIdList(List<Long> orderIdList);

    /**
     * 获取所有属于当前账单的订单，按分摊是的顺序
     */
    List<OrderFeeStatement> queryOrderFeeStatementByBillNumber(String billNumber);

    List<OrderFeeStatement> lists(Long id, Long tenantId, String beginDate, String endDate);


}
