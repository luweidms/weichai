package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.dto.*;
import com.youming.youche.order.dto.order.OrderListAppDto;
import com.youming.youche.order.dto.order.OrderListWxDto;
import com.youming.youche.order.vo.OrderListAppVo;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.OrderListWxVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/3/9 15:39
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 查询车队在途订单车辆
     *
     * @param tenantId 司机ID
     */
    List<Map> queryPlateNumberOrderByTenantId(@Param("tenantId") Long tenantId);


    /**
     * 查询订单列表
     *
     * @param orderListIn
     * @return
     */
    IPage<OrderListOut> getOrderListOut(Page<OrderListOut> page, @Param("orderListIn") OrderListInVo orderListIn, @Param("orderIds") List<String> orderIds, @Param("tenantId") Long tenantId);

    /**
     * 获取订单成本信息
     */
    List<Map> queryOrderCostInfo(@Param("orderId") Long orderId);

    /**
     * 营运工作台  改单审核数量
     *
     * @return
     */
    List<WorkbenchDto> getTableModifyExamineCount();

    /**
     * 营运工作台  异常审核数量
     */
    List<WorkbenchDto> getTableExceptionExamineCount();

    /**
     * 营运工作台  回单审核数量
     */
    List<WorkbenchDto> getTableReceiptExamineCount();

    List<WorkbenchDto> getTableBillTodayCount(@Param("localDateTime") LocalDateTime localDateTime);

    List<WorkbenchDto> getTableBillTodayHCount(@Param("localDateTime") LocalDateTime localDateTime);

    /**
     * 营运工作台  待付付数量
     */
    List<WorkbenchDto> getTablePrepaidOrderCount();

    /**
     * 营运工作台  在途数量
     */
    List<WorkbenchDto> getTableIntransitOrderCount();

    /**
     * 营运工作台  待回单审核
     */
    List<WorkbenchDto> getTableRetrialOrderCount();

    /**
     * 微信查询订单列表(30016)
     *
     * @param page
     * @param orderListWxVo
     * @return
     */
    Page<OrderListWxDto> queryOrderListWxOut(@Param("page") Page<OrderListWxOutDto> page, @Param("orderListWxVo") OrderListWxVo orderListWxVo);

    List<OrderListAppDto> queryOrderListAppOut(@Param("vo") OrderListAppVo vo);

    /**
     * APP查询订单列表(30001)
     * @param vo
     * @return
     */
    Page<OrderListAppOutDto> queryOrderListApp(@Param("page") Page<OrderListAppOutDto> page, @Param("vo") OrderListAppVo vo);

    /**
     * 查询车队在途订单
     *
     * @param tenantId 车队ID
     */
    List<OrderInfo> queryOrderInfoByTenantId(@Param("tenantId") Long tenantId);

    List<OrderInfoSalaryDto> queryOrderInfoSalary(@Param("salaryBeginDate") Date salaryBeginDate, @Param("salaryBeginDate") Date salaryEndDate);

    /**
     * 小程序首页订单审核统计 异常审核、回单审核
     */
    List<Long> getStatisticsOrderReview(@Param("orderListWxVo") OrderListWxVo orderListWxVo);

    /**
     * 车辆报表获取车辆订单部分邮费和etc
     */
    VehicleFeeFromOrderDataDto getVehicleFeeFromOrderDataByMonth(@Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId, @Param("month") String month);

    /**
     * 车辆报表获取车辆当前时间的车辆运输线路
     */
    String getVehicleLineFromOrderDataByMonth(@Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId);

    /**
     * 修改订单司机读取状态
     */
    void updateOrderIdMessage(@Param("orderId") Long orderId);

}
