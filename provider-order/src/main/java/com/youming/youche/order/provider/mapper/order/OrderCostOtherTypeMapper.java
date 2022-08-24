package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderCostOtherType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author xxx
* @since 2022-03-29
*/
    public interface OrderCostOtherTypeMapper extends BaseMapper<OrderCostOtherType> {
@Select("SELECT * from order_cost_other_type where tenant_id =#{tenantId} and type_name =#{typeName}  ORDER BY sort_num,id")
    List<OrderCostOtherType> getOrderCostOtherTypeList(long tenantId, String typeName);
@Select("select count(*) typeCount from order_cost_other_type  where  TENANT_Id=-1 or TENANT_Id=#{tenantId}")
    Long getOrderCostOtherTypeCount(Long tenantId);
}
