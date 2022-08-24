package com.youming.youche.order.provider.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderCostDetailReport;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.domain.order.OrderMainReport;
import com.youming.youche.order.dto.order.OrderCostReportDto;
import com.youming.youche.order.vo.OrderCostOtherReportVO;
import com.youming.youche.order.vo.OrderCostOtherTypeVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface OrderCostReportMapper extends BaseMapper<OrderCostReport> {
    @Select("select   *from  order_cost_other_type  where  tenant_id=#{tenantId}   ORDER BY sort_num ,id ")
    List<OrderCostOtherTypeVO> OrderCostOtherTypeList(long tenantId, String typeName);

    @Select("SELECT *from order_cost_detail_report where order_id = #{orderId}")
    OrderMainReport getObjectByOrderId(Long orderId);

    List<OrderCostReportDto> getListById(Long orderId);

    List<OrderCostDetailReport> getOrderCostDetailReport(Long id);

    List<OrderCostDetailReport> getOrderCostDetailReports(Long id);

    List<OrderCostOtherReportVO> getOrderCostOtherReport(Long id);

    List<OrderCostOtherReportVO> getOrderCostOtherReports(Long id);

    @Select("SELECT * from order_cost_report where  order_id =#{orderId} and state<> 3 and  state<> 5")
    List<OrderCostReport> getOrderCostReportByOrderId(Long orderId);

    /***
     * 根据订单号查询油费和加油升数的总和
     * @param orderIds
     * @return totalAmt 油费总和
     * @return totalOil 加油升数的总和
     */
    Map getTotalAmtByOrderIds(@Param("orderIds") String orderIds);

    List<Map> getKilometreByOrderIds(@Param("orders") String orders);

    /***
     * 根据订单号查询路桥费费总和
     * @param orderId
     * @return totalAmt 路桥费费总和
     */
    Map getTotalAmtByOrderId(@Param("orderId") Long orderId, @Param("tableType") Integer tableType);

    /**
     * 统计上报其他费用的消费总金额
     * @param orderId
     * @return
     */
    Long getOrderCostOtherReportAmountByOrderId(@Param("orderId") Long orderId);

}
