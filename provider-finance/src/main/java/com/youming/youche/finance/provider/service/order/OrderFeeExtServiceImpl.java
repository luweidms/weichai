package com.youming.youche.finance.provider.service.order;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderFeeExtService;
import com.youming.youche.finance.domain.order.OrderFeeExt;
import com.youming.youche.finance.provider.mapper.order.OrderFeeExtMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
* <p>
    * 订单费用扩展表 服务实现类
    * </p>
* @author liangyan
* @since 2022-03-15
*/
@DubboService(version = "1.0.0")
public class OrderFeeExtServiceImpl extends BaseServiceImpl<OrderFeeExtMapper, OrderFeeExt> implements IOrderFeeExtService {

    @Resource
    private OrderFeeExtMapper orderFeeExtMapper;

    @Override
    public OrderFeeExt getOrderFeeExtByOrderId(Long orderId) throws Exception {

        QueryWrapper<OrderFeeExt> orderFeeExtQueryWrapper = new QueryWrapper<>();
        orderFeeExtQueryWrapper.eq("order_id",orderId);
        List<OrderFeeExt> orderFeeExts = orderFeeExtMapper.selectList(orderFeeExtQueryWrapper);
        if(orderFeeExts != null && orderFeeExts.size() > 0){
            return orderFeeExts.get(0);
        }

        return null;
    }
    @Override
    public List<OrderFeeExt> selectFee(Long id, Long tenantId,String beginDate, String endDate) {
        List<OrderFeeExt> orderFeeExts = orderFeeExtMapper.selectFee(id,tenantId,beginDate,endDate);
        return orderFeeExts;
    }
}
