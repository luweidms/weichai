package com.youming.youche.finance.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.order.OrderReceipt;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单-回单
 *
 * @author hzx
 * @date 2022/2/18 10:59
 */
public interface IOrderReceiptService extends IBaseService<OrderReceipt> {

    /**
     * 查找订单回单
     * optType==2查看
     */
    List<OrderReceipt> findOrderReceipts(Long orderId, Long optType) throws Exception;

    /**
     * 查询订单对应客户单号
     */
    List<Map<String, Object>> getCustomerIdByOrderIdStrs(String orderIds);

}
