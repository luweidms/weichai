package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.finance.domain.order.OrderFeeStatementH;

import java.util.List;

/**
 * <p>
 * 订单账单信息历史表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-06
 */
public interface IOrderFeeStatementHService extends IBaseService<OrderFeeStatementH> {

    /**
     * 根据订单s查询订单账单历史记录s
     *
     * @param orderIdList 订单
     * @return
     */
    List<OrderFeeStatementH> getOrderFreeStatementHListByOrderIdList(List<Long> orderIdList);

    /**
     * 获取所有属于当前账单的订单，按分摊是的顺序
     *
     * @param billNumber 账单
     * @return
     */
    List<OrderFeeStatementH> queryOrderFeeStatementHByBillNumber(String billNumber);

    /**
     * 判断订单是否存在账单记录
     *
     * @param orderId
     * @return 账单号
     */
    String judgeDoesItExistOrderId(String orderId);



}
