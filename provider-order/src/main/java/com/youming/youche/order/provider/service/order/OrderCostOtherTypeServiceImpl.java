package com.youming.youche.order.provider.service.order;

import com.youming.youche.order.api.order.IOrderCostOtherTypeService;
import com.youming.youche.order.domain.order.OrderCostOtherType;
import com.youming.youche.order.provider.mapper.order.OrderCostOtherTypeMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* <p>
    *  服务实现类
    * </p>
* @author xxx
* @since 2022-03-29
*/
@DubboService(version = "1.0.0")
    public class OrderCostOtherTypeServiceImpl extends BaseServiceImpl<OrderCostOtherTypeMapper, OrderCostOtherType> implements IOrderCostOtherTypeService {


    @Override
    public List<OrderCostOtherType> getOrderCostOtherTypeList(long tenantId, String typeName) {
        List<OrderCostOtherType>  list =null;

        if(StringUtils.isNotBlank(typeName)) {
            list =baseMapper.getOrderCostOtherTypeList(tenantId,typeName);
        }
        return list();
    }

    @Override
    public Long getOrderCostOtherTypeCount(Long tenantId) {
        return baseMapper.getOrderCostOtherTypeCount(tenantId);
    }
}
