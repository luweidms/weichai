package com.youming.youche.finance.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.api.order.IOrderReceiptService;
import com.youming.youche.finance.domain.order.OrderReceipt;
import com.youming.youche.finance.provider.mapper.order.OrderReceiptMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 订单-回单
 *
 * @author hzx
 * @date 2022/2/18 11:00
 */
@DubboService(version = "1.0.0")
public class OrderReceiptServiceImpl extends BaseServiceImpl<OrderReceiptMapper, OrderReceipt> implements IOrderReceiptService {

    @Resource
    private OrderReceiptMapper orderReceiptMapper;

    @Override
    public List<OrderReceipt> findOrderReceipts(Long orderId, Long optType) throws Exception {
        List<OrderReceipt> orderRecipts = orderReceiptMapper.findOrderRecipts(orderId, null);
        if (optType == 2) {
            FastDFSHelper client = FastDFSHelper.getInstance();
            for (OrderReceipt orderReceipt : orderRecipts) {
                orderReceipt.setFlowUrl(client.getHttpURL(orderReceipt.getFlowUrl()).split("\\?")[0]);
            }
        }
        return orderRecipts;
    }

    @Override
    public List<Map<String, Object>> getCustomerIdByOrderIdStrs(String orderIds) {
        return baseMapper.getCustomerIdByOrderIds(orderIds);
    }

}
