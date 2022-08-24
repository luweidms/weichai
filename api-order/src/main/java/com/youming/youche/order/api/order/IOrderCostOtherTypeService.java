package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderCostOtherType;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author xxx
* @since 2022-03-29
*/
    public interface IOrderCostOtherTypeService extends IBaseService<OrderCostOtherType> {

    /**
     * 查询其他费用类型
     * @param tenantId
     * @param typeName
     * @return
     */
    List<OrderCostOtherType> getOrderCostOtherTypeList(long tenantId, String typeName);

    /**
     * 获取上报其他费用类型
     * @param tenantId
     * @return
     */
    Long getOrderCostOtherTypeCount(Long tenantId);
}
