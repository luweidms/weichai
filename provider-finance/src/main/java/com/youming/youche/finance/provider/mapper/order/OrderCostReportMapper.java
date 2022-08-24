package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.finance.domain.order.OrderCostReport;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.order.OrderCostReportDto;
import com.youming.youche.finance.dto.order.OrderMainReportDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
public interface OrderCostReportMapper extends BaseMapper<OrderCostReport> {


    /**
     * 接口说明 获取费用上报列表
     * 聂杰伟
     * 2022-3-8
     *
     * @param page
     * @param
     * @return
     */
    IPage<OrderCostReportDto> selectReport( @Param("orderId") String orderId,
                                           @Param("plateNumber") String plateNumber,
                                           @Param("userName") String userName,
                                           @Param("linkPhone") String linkPhone,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("subUserName") String subUserName,
                                           @Param("state") Integer state,
                                           @Param("sourceRegion") Integer sourceRegion,
                                           @Param("desRegion") Integer desRegion,
                                           @Param("tenantId") Long tenantId,
                                           @Param("lids")List<Long> lids,
                                           @Param("waitDeal") Boolean waitDeal,
                                           Page<OrderCostReportDto> page);


    //查询其他费用上报明细
    List<OrderMainReportDto> queryOrderCostDetailReports(
                                                          @Param("paymentWay")Integer paymentWay,
                                                          @Param("typeId") Long typeId,
                                                          @Param("orderId")Long orderId,
                                                          @Param("plateNumber")String plateNumber,
                                                          @Param("carDriverMan")String carDriverMan,
                                                          @Param("cardNo")String cardNo,
                                                          @Param("getVehicleExpenseDto") GetVehicleExpenseDto getVehicleExpenseDto,
                                                          @Param("tenantId") Long tenantId);


    /**
     * 车辆报表获取订单费用上报数据
     */
    List<VehicleMiscellaneouFeeDto> getVehicleOrderFeeByMonth(@Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId, @Param("month") String month);

}
