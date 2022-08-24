package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderCostOtherReport;
import com.youming.youche.order.vo.OrderCostOtherReportVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author xxx
* @since 2022-03-29
*/
    public interface OrderCostOtherReportMapper extends BaseMapper<OrderCostOtherReport> {
        @Select("SELECT * from order_cost_other_report where  rel_id =#{relId} and state<> 3 and  state<> 5")
    List<OrderCostOtherReportVO> getOrderCostOtherReport(Long relId);
}
