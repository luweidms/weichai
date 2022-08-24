package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * Mapper接口
 * </p>
 * @author Terry
 * @since 2022-03-09
 */
public interface OrderCostOtherReportMapper extends BaseMapper<OrderCostOtherReport> {

    List<OrderCostOtherReport> selectFee(@Param("id") Long id, @Param("tenantId") Long tenantId,@Param("beginDate")String beginDate, @Param("endDate")String endDate);

}
